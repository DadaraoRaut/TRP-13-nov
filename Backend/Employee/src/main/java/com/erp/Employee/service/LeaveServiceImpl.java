package com.erp.Employee.service;

import com.erp.Employee.dto.EmployeeDTO;
import com.erp.Employee.dto.LeaveRequestDTO;
import com.erp.Employee.enitites.Employee;
import com.erp.Employee.enitites.LeaveBalance;
import com.erp.Employee.enitites.LeaveRequest;
import com.erp.Employee.repository.LeaveBalanceRepository;
import com.erp.Employee.repository.LeaveRepository;
import com.erp.Employee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveServiceImpl implements LeaveService {

    @Autowired
    private LeaveRepository leaveRepo;

    @Autowired
    private LeaveBalanceRepository balanceRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${admin-service.base-url}")
    private String adminServiceUrl;

    private static final double MAX_CASUAL = 12;
    private static final double MAX_SICK = 10;
    private static final double MAX_EARNED = 15;

    // =================== Extract Employee ID from JWT ===================
    private String extractEmployeeIdFromToken() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            throw new RuntimeException("No active request context found");
        }

        String authHeader = attr.getRequest().getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        return jwtUtil.extractEmployeeId(token); // assuming username = employeeId
    }

    // =================== Fetch Employee from Admin Service ===================
    private EmployeeDTO fetchEmployeeFromAdminService(String employeeId) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = attr.getRequest().getHeader("Authorization"); // Bearer <token>

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = adminServiceUrl + employeeId;
        ResponseEntity<EmployeeDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, EmployeeDTO.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Employee not found in Admin Service: " + employeeId);
        }

        return response.getBody();
    }

    // =================== Apply Leave ===================
    @Override
    public LeaveRequestDTO applyLeave(LeaveRequestDTO dto) {
        String employeeId = extractEmployeeIdFromToken();
        fetchEmployeeFromAdminService(employeeId);
        dto.setEmployeeId(employeeId);

        double leaveDays = "HalfDay".equalsIgnoreCase(dto.getLeaveType()) ? 0.5 :
                ChronoUnit.DAYS.between(dto.getFromDate(), dto.getToDate()) + 1;

        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId)
                .orElseGet(() -> {
                    LeaveBalance lb = new LeaveBalance();
                    lb.setEmployeeId(employeeId);
                    lb.setCasual(MAX_CASUAL);
                    lb.setSick(MAX_SICK);
                    lb.setEarned(MAX_EARNED);
                    lb.setCompOff(0);
                    return lb;
                });

        double available = switch (dto.getLeaveType().toLowerCase()) {
            case "casual", "halfday" -> balance.getCasual();
            case "sick" -> balance.getSick();
            case "earned" -> balance.getEarned();
            case "compoff" -> balance.getCompOff();
            default -> 0;
        };

        if (available < leaveDays) {
            throw new RuntimeException("Insufficient leave balance");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployeeId(employeeId);
        leave.setLeaveType(dto.getLeaveType());
        leave.setFromDate(dto.getFromDate());
        leave.setToDate(dto.getToDate());
        leave.setReason(dto.getReason());
        leave.setAppliedOn(java.time.LocalDate.now());
        leave.setStatus("Pending");
        leave.setCcTo(dto.getCcTo());
        leave.setContactDetails(dto.getContactDetails());
        leaveRepo.save(leave);

        // Deduct leave
        switch (dto.getLeaveType().toLowerCase()) {
            case "casual", "halfday" -> balance.setCasual(balance.getCasual() - leaveDays);
            case "sick" -> balance.setSick(balance.getSick() - leaveDays);
            case "earned" -> balance.setEarned(balance.getEarned() - leaveDays);
            case "compoff" -> balance.setCompOff(balance.getCompOff() - leaveDays);
        }
        balanceRepo.save(balance);

        return convertToDTO(leave, balance, dto.getLeaveType());
    }

    // =================== Cancel Leave (latest) ===================
    @Override
    public String cancelLeaveByEmployeeId(String unused) {
        String employeeId = extractEmployeeIdFromToken();
        fetchEmployeeFromAdminService(employeeId);

        LeaveRequest leave = leaveRepo.findByEmployeeIdOrderByAppliedOnDesc(employeeId)
                .stream()
                .filter(l -> !"Cancelled".equalsIgnoreCase(l.getStatus()))
                .findFirst()
                .orElse(null);

        if (leave == null) {
            return "No pending or approved leave found for employee " + employeeId;
        }

        return cancelLeaveAndRestoreBalance(leave);
    }

    // =================== Cancel Leave by ID ===================
    @Override
    public LeaveRequestDTO cancelLeave(Long id) {
        String employeeId = extractEmployeeIdFromToken();
        LeaveRequest leave = leaveRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));

        if (!leave.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("You are not authorized to cancel this leave");
        }

        if (!"Pending".equalsIgnoreCase(leave.getStatus())) {
            throw new RuntimeException("Only pending leaves can be cancelled.");
        }

        cancelLeaveAndRestoreBalance(leave);
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId)
                .orElse(new LeaveBalance());
        return convertToDTO(leave, balance, leave.getLeaveType());
    }



    // =================== Cancel Leave Helper ===================
    private String cancelLeaveAndRestoreBalance(LeaveRequest leave) {
        LeaveBalance balance = balanceRepo.findByEmployeeId(leave.getEmployeeId())
                .orElseGet(() -> {
                    LeaveBalance lb = new LeaveBalance();
                    lb.setEmployeeId(leave.getEmployeeId());
                    lb.setCasual(MAX_CASUAL);
                    lb.setSick(MAX_SICK);
                    lb.setEarned(MAX_EARNED);
                    lb.setCompOff(0);
                    return lb;
                });

        double leaveDays = "HalfDay".equalsIgnoreCase(leave.getLeaveType()) ? 0.5 :
                ChronoUnit.DAYS.between(leave.getFromDate(), leave.getToDate()) + 1;

        if (!"Cancelled".equalsIgnoreCase(leave.getStatus())) {
            switch (leave.getLeaveType().toLowerCase()) {
                case "casual", "halfday" -> balance.setCasual(Math.min(balance.getCasual() + leaveDays, MAX_CASUAL));
                case "sick" -> balance.setSick(Math.min(balance.getSick() + leaveDays, MAX_SICK));
                case "earned" -> balance.setEarned(Math.min(balance.getEarned() + leaveDays, MAX_EARNED));
                case "compoff" -> balance.setCompOff(balance.getCompOff() + leaveDays);
            }

            balanceRepo.save(balance);
            leave.setStatus("Cancelled");
            leaveRepo.save(leave);
            return "Leave for employee " + leave.getEmployeeId() + " has been cancelled and balance restored.";
        } else {
            return "Leave already cancelled for employee " + leave.getEmployeeId();
        }
    }
    @Override
    public LeaveBalance getLeaveBalance(String employeeId) {
        return balanceRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Leave balance not found for employee: " + employeeId));
    }
    // =================== Get All Leaves for Logged-In Employee ===================
    @Override
    public List<LeaveRequestDTO> getLeavesByEmployee(String unused) {
        String employeeId = extractEmployeeIdFromToken();
        fetchEmployeeFromAdminService(employeeId);
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId).orElse(new LeaveBalance());
        return leaveRepo.findByEmployeeId(employeeId).stream()
                .map(l -> convertToDTO(l, balance, l.getLeaveType()))
                .collect(Collectors.toList());
    }

    // =================== Get Pending Leaves ===================
    @Override
    public List<LeaveRequestDTO> getPendingLeaves(String unused) {
        String employeeId = extractEmployeeIdFromToken();
        fetchEmployeeFromAdminService(employeeId);
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId).orElse(new LeaveBalance());
        return leaveRepo.findByEmployeeIdAndStatus(employeeId, "Pending")
                .stream()
                .map(l -> convertToDTO(l, balance, l.getLeaveType()))
                .collect(Collectors.toList());
    }

    // =================== Leave History ===================
    @Override
    public List<LeaveRequestDTO> getLeaveHistory(String unused) {
        String employeeId = extractEmployeeIdFromToken();
        fetchEmployeeFromAdminService(employeeId);
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId).orElse(new LeaveBalance());
        return leaveRepo.findByEmployeeIdAndStatusIn(employeeId, List.of("Approved", "Cancelled"))
                .stream()
                .map(l -> convertToDTO(l, balance, l.getLeaveType()))
                .collect(Collectors.toList());
    }

    // =================== Grant CompOff ===================
    @Override
    public void grantCompOff(String employeeId, int days) {
        // NOTE: Keep this admin-only in controller layer
        fetchEmployeeFromAdminService(employeeId);
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId)
                .orElseGet(() -> {
                    LeaveBalance lb = new LeaveBalance();
                    lb.setEmployeeId(employeeId);
                    lb.setCasual(MAX_CASUAL);
                    lb.setSick(MAX_SICK);
                    lb.setEarned(MAX_EARNED);
                    lb.setCompOff(0);
                    return lb;
                });
        balance.setCompOff(balance.getCompOff() + days);
        balanceRepo.save(balance);
    }

    // =================== Convert Entity to DTO ===================
    private LeaveRequestDTO convertToDTO(LeaveRequest leave, LeaveBalance balance, String leaveType) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setId(leave.getId());
        dto.setEmployeeId(leave.getEmployeeId());
        dto.setLeaveType(leave.getLeaveType());
        dto.setFromDate(leave.getFromDate());
        dto.setToDate(leave.getToDate());
        dto.setReason(leave.getReason());
        dto.setAppliedOn(leave.getAppliedOn());
        dto.setStatus(leave.getStatus());
        dto.setCcTo(leave.getCcTo());
        dto.setContactDetails(leave.getContactDetails());

        double remaining = switch (leaveType.toLowerCase()) {
            case "casual", "halfday" -> balance.getCasual();
            case "sick" -> balance.getSick();
            case "earned" -> balance.getEarned();
            case "compoff" -> balance.getCompOff();
            default -> 0;
        };
        dto.setRemainingBalance(remaining);
        return dto;
    }

    @Override
    public List<Employee> getAllEmployees() {
        throw new UnsupportedOperationException("Use Admin Service to fetch all employees");
    }

    @Override
    public void cancelLeavesByEmployee(String employeeId) {
        throw new UnsupportedOperationException("Handled via cancelLeaveByEmployeeId()");
    }
}
