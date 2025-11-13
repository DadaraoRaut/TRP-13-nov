package com.erp.Employee.repository;

import com.erp.Employee.enitites.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Optional<Salary> findByEmployeeIdAndSalaryMonth(String employeeId, String salaryMonth);
    List<Salary> findByEmployeeId(String employeeId);
}
