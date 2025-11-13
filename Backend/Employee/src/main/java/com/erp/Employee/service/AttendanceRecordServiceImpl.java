package com.erp.Employee.service;

import com.erp.Employee.dto.AttendanceRecordDTO;
import com.erp.Employee.dto.EmployeeDTO;
import com.erp.Employee.enitites.AttendanceRecord;
import com.erp.Employee.repository.AttendanceRecordRepository;
import com.erp.Employee.repository.EmployeeRepository;
import com.erp.Employee.repository.LeaveRepository;
import com.erp.Employee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordRepository repository;
    private final EmployeeRepository emprepository;
    private final LeaveRepository leaveRepo;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;


    @Value("${admin-service.base-url}")
    private String adminServiceUrl;

    public AttendanceRecordServiceImpl(
            AttendanceRecordRepository repository,
            EmployeeRepository emprepository,
            LeaveRepository leaveRepo,
            RestTemplate restTemplate,
            JwtUtil jwtUtil) {
        this.repository = repository;
        this.emprepository = emprepository;
        this.leaveRepo = leaveRepo;
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }

    // ---------------- SIGN-IN ----------------
    public AttendanceRecordDTO signIn(AttendanceRecordDTO dto) {
        final String employeeId = getEmployeeIdFromToken(); // ✅ from JWT, not from frontend

        EmployeeDTO employee = fetchEmployeeFromAdminService(employeeId);

        LocalDate attendanceDate = dto.getDate() != null
                ? dto.getDate()
                : LocalDate.now();

        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(employeeId);
        record.setDate(attendanceDate);
        LocalDateTime now = LocalDateTime.now();
        record.setSignInTime(now);
        record.setFirstIn(now);

        record.setShift("09:30-18:30");
        record.setShiftStart(attendanceDate.atTime(9, 30));
        record.setShiftEnd(attendanceDate.atTime(18, 30));

        record.setStatus(now.toLocalTime().isAfter(LocalTime.of(9, 30)) ? "Late" : "P");

        AttendanceRecord saved = repository.save(record);
        return convertToDTO(saved, employee);
    }


    // ---------------- SIGN-OUT ----------------
//    public AttendanceRecordDTO signOut(AttendanceRecordDTO dto) {
//        final String employeeId = getEmployeeIdFromToken(); // ✅ from JWT
//
//        EmployeeDTO employee = fetchEmployeeFromAdminService(employeeId);
//
//        LocalDate attendanceDate = dto.getDate() != null ? dto.getDate() : LocalDate.now();
//
//        List<AttendanceRecord> openRecords = repository.findByEmployeeIdAndDate(employeeId, attendanceDate)
//                .stream()
//                .filter(r -> r.getSignOutTime() == null)
//                .toList();
//
//        LocalDateTime now = LocalDateTime.now();
//
//        if (openRecords.isEmpty()) {
//            AttendanceRecord record = new AttendanceRecord();
//            record.setEmployeeId(employeeId);
//            record.setDate(attendanceDate);
//            record.setSignOutTime(now);
//            record.setLastOut(now);
//            AttendanceRecord saved = repository.save(record);
//            return convertToDTO(saved, employee);
//        } else {
//            AttendanceRecord record = openRecords.get(openRecords.size() - 1);
//            record.setSignOutTime(now);
//            record.setLastOut(now);
//            recalcWorkHours(record);
//            repository.save(record);
//            return convertToDTO(record, employee);
//        }
//    }
    public AttendanceRecordDTO signOut(AttendanceRecordDTO dto) {
        final String employeeId = getEmployeeIdFromToken();
        EmployeeDTO employee = fetchEmployeeFromAdminService(employeeId);
        LocalDate attendanceDate = dto.getDate() != null ? dto.getDate() : LocalDate.now();

        LocalDateTime now = LocalDateTime.now();

        // ✅ Try to find today's record for the employee
        List<AttendanceRecord> recordsToday = repository.findByEmployeeIdAndDate(employeeId, attendanceDate);

        AttendanceRecord record;

        if (recordsToday.isEmpty()) {
            // No record exists — maybe user missed sign-in
            record = new AttendanceRecord();
            record.setEmployeeId(employeeId);
            record.setDate(attendanceDate);
            record.setSignInTime(null); // no sign-in
            record.setSignOutTime(now);
            record.setFirstIn(null);
            record.setLastOut(now);
            record.setStatus("Missed Sign-in");
        } else {
            // ✅ Update existing record
            record = recordsToday.get(recordsToday.size() - 1);

            // Don't overwrite sign-in time
            if (record.getSignInTime() == null) {
                record.setSignInTime(record.getFirstIn());
            }

            record.setSignOutTime(now);
            record.setLastOut(now);
            recalcWorkHours(record);
        }

        repository.save(record);
        return convertToDTO(record, employee);
    }

    private String getEmployeeIdFromToken() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authHeader = attr.getRequest().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        return jwtUtil.extractEmployeeId(authHeader.substring(7)); // assuming username = employeeId
    }


    // ---------------- MARK ATTENDANCE ----------------
    @Override
    public AttendanceRecordDTO markAttendance(AttendanceRecordDTO dto) {
        if (dto.getSignInTime() != null) return signIn(dto);
        if (dto.getSignOutTime() != null) return signOut(dto);
        throw new RuntimeException("Invalid attendance data");
    }

    // ---------------- GET ALL ATTENDANCE ----------------
//    @Override
//    public List<AttendanceRecordDTO> getAll() {
//        String tokenEmployeeId = getEmployeeIdFromToken();
////        String role = jwtUtil.extractRole(getTokenFromRequest());
//
//        List<AttendanceRecord> records;
//
////        if ("ADMIN".equalsIgnoreCase(role)) {
////            records = repository.findAll(); // Admin can view all
////        } else {
////            records = repository.findByEmployeeId(tokenEmployeeId); // Only own records
////        }
//
//        return records.stream()
//              .collect(Collectors.toList());
//    }          .map(r -> convertToDTO(r, null))


    private String getTokenFromRequest() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attr.getRequest().getHeader("Authorization");
    }


    // ---------------- HELPER METHODS ----------------
    private void recalcWorkHours(AttendanceRecord record) {
        if (record.getFirstIn() != null && record.getLastOut() != null) {
            Duration workDuration = Duration.between(record.getFirstIn(), record.getLastOut());
            record.setWorkHrs(formatDuration(workDuration));

            int lateMinutes = (int) ChronoUnit.MINUTES.between(record.getShiftStart().toLocalTime(), record.getFirstIn().toLocalTime());
            record.setLateIn(lateMinutes > 0 ? formatMinutesToHHMM(lateMinutes) : "00:00");

            int earlyMinutes = (int) ChronoUnit.MINUTES.between(record.getLastOut().toLocalTime(), record.getShiftEnd().toLocalTime());
            record.setEarlyOut(earlyMinutes > 0 ? formatMinutesToHHMM(earlyMinutes) : "00:00");

            calculateWorkAndShortfall(record);
        }
    }

    private void calculateWorkAndShortfall(AttendanceRecord record) {
        int breakMinutes = 30;
        record.setBreakHrs(formatMinutesToHHMM(breakMinutes));

        if (record.getFirstIn() != null && record.getLastOut() != null) {
            int totalMinutes = (int) Duration.between(record.getFirstIn(), record.getLastOut()).toMinutes();
            int actualMinutes = totalMinutes - breakMinutes;
            int shiftMinutes = 9 * 60;

            record.setTotalWorkHrs(formatMinutesToHHMM(totalMinutes));
            record.setActualWorkHrs(formatMinutesToHHMM(actualMinutes));

            if (actualMinutes < shiftMinutes) {
                record.setShortfallHrs(formatMinutesToHHMM(shiftMinutes - actualMinutes));
                record.setExcessHrs("00:00");
            } else {
                record.setExcessHrs(formatMinutesToHHMM(actualMinutes - shiftMinutes));
                record.setShortfallHrs("00:00");
            }
        }
    }

    // ✅ Final correct DTO mapper (includes employee info if available)
    private AttendanceRecordDTO convertToDTO(AttendanceRecord record, EmployeeDTO employee) {
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        dto.setEmployeeId(record.getEmployeeId());
        dto.setDate(record.getDate());
        dto.setSignInTime(record.getSignInTime());
        dto.setSignOutTime(record.getSignOutTime());
        dto.setFirstIn(record.getFirstIn());
        dto.setLastOut(record.getLastOut());
        dto.setStatus(record.getStatus());
        dto.setTotalWorkHrs(record.getTotalWorkHrs() != null ? record.getTotalWorkHrs() : "00:00");
        dto.setActualWorkHrs(record.getActualWorkHrs() != null ? record.getActualWorkHrs() : "00:00");
        dto.setBreakHrs(record.getBreakHrs() != null ? record.getBreakHrs() : "00:30");
        dto.setShortfallHrs(record.getShortfallHrs() != null ? record.getShortfallHrs() : "00:00");
        dto.setExcessHrs(record.getExcessHrs() != null ? record.getExcessHrs() : "00:00");
        dto.setWorkHrs(record.getWorkHrs());
        dto.setShift(record.getShift());
        dto.setLateIn(record.getLateIn());
        dto.setEarlyOut(record.getEarlyOut());
        dto.setSession1Start(record.getSession1Start());
        dto.setSession1End(record.getSession1End());
        dto.setSession2Start(record.getSession2Start());
        dto.setSession2End(record.getSession2End());
        dto.setShiftStart(record.getShiftStart());
        dto.setShiftEnd(record.getShiftEnd());
        dto.setName(record.getName());
//        if (employee != null) {
//            dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
//            dto.setDepartment(employee.getDepartment());
//            dto.setDesignation(employee.getDesignation());
//        }
        return dto;
    }

    private String formatMinutesToHHMM(int minutes) {
        int hrs = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hrs, mins);
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02d:%02d", hours, minutes);
    }

    // ---------------- FETCH EMPLOYEE FROM ADMIN SERVICE ----------------
    public EmployeeDTO fetchEmployeeFromAdminService(String employeeId) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = attr.getRequest().getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("JWT token missing in request");
        }

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

//    @Override
//    public List<AttendanceRecordDTO> getAll() {
//        List<AttendanceRecord> records = repository.findAll();
//
//        return records.stream()
//                .map(record -> convertToDTO(record, null))
//                .collect(Collectors.toList());
//    }
    @Override
    public List<AttendanceRecordDTO> getAll() {
        String employeeId = getEmployeeIdFromToken();
        List<AttendanceRecord> records = repository.findByEmployeeId(employeeId);
        return records.stream()
                .map(record -> convertToDTO(record, null))
                .collect(Collectors.toList());
    }


    @Override
    public List<AttendanceRecordDTO> getDailyAttendance(String employeeId, YearMonth month) {
        return null;
    }

    @Override
    public AttendanceRecordDTO getMonthlyAttendance(String employeeId, int year, int month) {
        return null;
    }
}
