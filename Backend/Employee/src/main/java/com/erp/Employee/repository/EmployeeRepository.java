package com.erp.Employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.Employee.enitites.Employee;
import com.erp.Employee.enitites.Salary;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByActiveTrue();
    boolean existsByEmail(String email);
    List<Employee> findAll();
    Optional<Employee> findByEmployeeId(String employeeId);
}
