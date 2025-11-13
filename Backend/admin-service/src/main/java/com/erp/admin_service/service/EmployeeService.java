package com.erp.admin_service.service;

import com.erp.admin_service.exception.ResourceNotFoundException;
import com.erp.admin_service.model.Employee;
import com.erp.admin_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    // 🔧 Auth Service base URL (can be moved to application.yml)
    @Value("${auth.service.url:http://localhost:8081/api/auth}")
    private String authServiceUrl;

    public EmployeeService(EmployeeRepository employeeRepository, EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
        this.restTemplate = new RestTemplate();
    }

    // ✅ Add new Employee (Admin-only)
    public Employee addEmployee(Employee employee) {
        // Generate a unique Employee ID
        String empId = "EMP" + String.format("%03d", new Random().nextInt(999));
        while (employeeRepository.findByEmployeeId(empId).isPresent()) {
            empId = "EMP" + String.format("%03d", new Random().nextInt(999));
        }

        // Generate random password
        String password = UUID.randomUUID().toString().substring(0, 8);

        employee.setEmployeeId(empId);
        employee.setPassword(password);
        employee.setRole("EMPLOYEE");

        // ✅ Save employee locally in Admin DB
        Employee savedEmployee = employeeRepository.save(employee);
        System.out.println("✅ Employee saved locally with ID: " + empId);

        // ✅ Send credentials email
        try {
            emailService.sendCredentials(employee.getEmail(), empId, password, "Employee");
            System.out.println("📧 Credentials sent to " + employee.getEmail());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send email: " + e.getMessage());
        }

        // ✅ Register employee in AuthService
        registerInAuthService(empId, password, "EMPLOYEE");

        return savedEmployee;
    }

    // 🔐 Register employee credentials in AuthService
    private void registerInAuthService(String username, String password, String role) {
        try {
            String url = authServiceUrl + "/register";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("role", role);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Registered employee in AuthService: " + username);
            } else {
                System.err.println("⚠️ AuthService registration failed: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("🚨 Error calling AuthService: " + e.getMessage());
        }
    }

    // ✅ Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // ✅ Get employee using EmployeeId (EMP001)
    public Employee getEmployeeByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee not found with EmployeeId: " + employeeId));
    }

    // ✅ Delete employee using EmployeeId
    public void deleteEmployee(String employeeId) {
        Employee emp = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cannot delete — Employee not found with EmployeeId: " + employeeId));

        employeeRepository.delete(emp);
        System.out.println("🗑️ Employee deleted: " + employeeId);
    }
}
