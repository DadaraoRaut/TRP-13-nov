package com.erp.Employee.controller;

import com.erp.Employee.dto.AttendanceRecordDTO;
import com.erp.Employee.dto.EmployeeDTO;
import com.erp.Employee.dto.LeaveRequestDTO;
import com.erp.Employee.dto.SalaryDTO;
import com.erp.Employee.enitites.Employee;
import com.erp.Employee.enitites.LeaveBalance;
import com.erp.Employee.repository.EmployeeRepository;
import com.erp.Employee.repository.LeaveBalanceRepository;
import com.erp.Employee.repository.LeaveRepository;
import com.erp.Employee.security.JwtUtil;
import com.erp.Employee.service.AttendanceRecordService;
import com.erp.Employee.service.AttendanceRecordServiceImpl;
import com.erp.Employee.service.LeaveService;
import com.erp.Employee.service.SalaryService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private LeaveBalanceRepository balanceRepo;
    private final AttendanceRecordService recordService;

    private final AttendanceRecordServiceImpl recordServiceimpl;
    private final LeaveService leaveService;
    private final SalaryService salaryService;
    private final LeaveRepository leaverepo;
    private final  JwtUtil jwtUtil;


    public EmployeeController(EmployeeRepository employeeRepo,
                              LeaveBalanceRepository balanceRepo,
                              AttendanceRecordService recordService,
                              LeaveService leaveService,
                              SalaryService salaryService,
                              LeaveRepository leaverepo,
                              AttendanceRecordServiceImpl recordServiceimpl,
                              JwtUtil jwtUtil) {
        this.employeeRepo = employeeRepo;
        this.balanceRepo = balanceRepo;
        this.recordService = recordService;
        this.leaveService = leaveService;
        this.salaryService = salaryService;
        this.leaverepo = leaverepo;
        this.recordServiceimpl = recordServiceimpl;
        this.jwtUtil = jwtUtil;
    }

    // ===================== Attendance =====================
    @PostMapping("/signin")
    public AttendanceRecordDTO signIn(HttpServletRequest request, @RequestBody AttendanceRecordDTO dto) {
        String employeeId = (String) request.getAttribute("employeeId");
        dto.setEmployeeId(employeeId);
        return recordService.markAttendance(dto);
    }

    @PutMapping("/signout")
    public AttendanceRecordDTO signOut(HttpServletRequest request, @RequestBody AttendanceRecordDTO dto) {
        String employeeId = (String) request.getAttribute("employeeId");
        dto.setEmployeeId(employeeId);
        return recordService.markAttendance(dto);
    }

    @GetMapping("/allattendance")
    public ResponseEntity<List<AttendanceRecordDTO>> getAll() {
        return ResponseEntity.ok(recordService.getAll());
    }

    @GetMapping("/attendance/{year}/{month}")
    public ResponseEntity<AttendanceRecordDTO> getEmployeeMonthlyAttendance(
            HttpServletRequest request,
            @PathVariable int year,
            @PathVariable int month) {
        String employeeId = (String) request.getAttribute("employeeId");
        AttendanceRecordDTO summary = recordService.getMonthlyAttendance(employeeId, year, month);
        return ResponseEntity.ok(summary);
    }
//    @GetMapping("/me")
//    public ResponseEntity<EmployeeDTO> getLoggedEmployeeDetails(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.substring(7);
//        String employeeId = jwtUtil.extractEmployeeId(token);
//
//        EmployeeDTO employee = recordServiceimpl.fetchEmployeeFromAdminService(employeeId);
//        return ResponseEntity.ok(employee);
//    }
@GetMapping("/me")
public ResponseEntity<EmployeeDTO> getLoggedEmployeeDetails(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String token = authHeader.substring(7);
    String employeeId = jwtUtil.extractEmployeeId(token);
    EmployeeDTO employee = recordServiceimpl.fetchEmployeeFromAdminService(employeeId);
    return ResponseEntity.ok(employee);
}



    // ===================== Leave =====================
    @PostMapping("/applyleave")
    public ResponseEntity<LeaveRequestDTO> applyLeave(HttpServletRequest request,
                                                      @RequestBody LeaveRequestDTO dto) {
        String employeeId = (String) request.getAttribute("employeeId");
        dto.setEmployeeId(employeeId);
        return ResponseEntity.ok(leaveService.applyLeave(dto));
    }

    @PutMapping("/cancel")
    public ResponseEntity<String> cancelLeaveByEmployee(HttpServletRequest request) {
        String employeeId = (String) request.getAttribute("employeeId");
        String message = leaveService.cancelLeaveByEmployeeId(employeeId);
        return ResponseEntity.ok(message);
    }
    @GetMapping("/balance")
    public ResponseEntity<LeaveBalance> getLeaveBalance(HttpServletRequest request) {
        String employeeId = (String) request.getAttribute("employeeId");
        LeaveBalance balance = balanceRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Leave balance not found for employee: " + employeeId));
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/allleave")
    public ResponseEntity<List<LeaveRequestDTO>> getLeavesByEmployee(HttpServletRequest request) {
        String employeeId = (String) request.getAttribute("employeeId");
        List<LeaveRequestDTO> leaves = leaveService.getLeavesByEmployee(employeeId);
        return ResponseEntity.ok(leaves);
    }

    @PostMapping("/compoff")
    public ResponseEntity<String> grantCompOff(HttpServletRequest request,
                                               @RequestBody Map<String, Object> body) {
        String employeeId = (String) request.getAttribute("employeeId");
        int days = (int) body.get("days");
        leaveService.grantCompOff(employeeId, days);
        return ResponseEntity.ok("Comp off granted successfully for " + employeeId);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingLeaves(HttpServletRequest request) {
        String employeeId = (String) request.getAttribute("employeeId");
        List<LeaveRequestDTO> pending = leaveService.getPendingLeaves(employeeId);

        List<Map<String, Object>> filtered = pending.stream()
                .map(leave -> Map.<String, Object>of(
                        "id", leave.getId(),
                        "employeeId", leave.getEmployeeId(),
                        "leaveType", leave.getLeaveType(),
                        "fromDate", leave.getFromDate(),
                        "toDate", leave.getToDate(),
                        "reason", leave.getReason(),
                        "appliedOn", leave.getAppliedOn(),
                        "status", leave.getStatus()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getLeaveHistory(HttpServletRequest request) {
        String employeeId = (String) request.getAttribute("employeeId");
        List<LeaveRequestDTO> history = leaveService.getLeaveHistory(employeeId);

        List<Map<String, Object>> filtered = history.stream()
                .map(leave -> Map.<String, Object>of(
                        "id", leave.getId(),
                        "employeeId", leave.getEmployeeId(),
                        "leaveType", leave.getLeaveType(),
                        "fromDate", leave.getFromDate(),
                        "toDate", leave.getToDate(),
                        "reason", leave.getReason(),
                        "appliedOn", leave.getAppliedOn(),
                        "status", leave.getStatus()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    // ===================== Salary =====================
    @PostMapping("/salaryslip")
    public ResponseEntity<SalaryDTO> generateSalary(HttpServletRequest request,
                                                    @RequestBody SalaryDTO dto) {
        String employeeId = (String) request.getAttribute("employeeId");
        dto.setEmployeeId(employeeId);
        return ResponseEntity.ok(salaryService.generateSalary(dto));
    }

//    @GetMapping("/pdfslip/{month}")
//    public ResponseEntity<byte[]> generatePayslipPdfFromDTO(@PathVariable String employeeId,
//                                                            @PathVariable String month) throws IOException {
//        ByteArrayInputStream pdfStream = salaryService.generatePayslipPdf(employeeId, month);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "inline; filename=" + employeeId + "-" + month + "-payslip.pdf");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfStream.readAllBytes());
//    }
    @GetMapping("/pdfslip/{month}")
    public ResponseEntity<byte[]> generatePayslipPdfFromDTO(HttpServletRequest request,
                                                            @PathVariable String month) throws IOException {
        String employeeId = (String) request.getAttribute("employeeId");

        ByteArrayInputStream pdfStream = salaryService.generatePayslipPdf(employeeId, month);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + employeeId + "-" + month + "-payslip.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.readAllBytes());
    }

}
