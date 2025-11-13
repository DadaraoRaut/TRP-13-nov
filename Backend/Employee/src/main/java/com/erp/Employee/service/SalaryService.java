package com.erp.Employee.service;

import com.erp.Employee.dto.SalaryDTO;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface SalaryService {
    SalaryDTO generateSalary(SalaryDTO dto);
    SalaryDTO getSalary(String employeeId, String month);
    List<SalaryDTO> getSalariesByEmployee(String employeeId);
    List<SalaryDTO> getAllSalaries();
    ByteArrayInputStream generatePayslipPdf(String employeeId, String month);
    String getEmployeeIdFromToken(String token); // via Admin Service using RestTemplate
    ByteArrayInputStream generatePayslipPdfFromDTO(SalaryDTO salaryDTO);


}
