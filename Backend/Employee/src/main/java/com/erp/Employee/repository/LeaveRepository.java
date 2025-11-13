package com.erp.Employee.repository;

import com.erp.Employee.enitites.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(String employeeId);
    List<LeaveRequest> findByEmployeeIdAndStatus(String employeeId, String status);

    List<LeaveRequest> findByEmployeeIdAndStatusIn(String employeeId, List<String> statuses);
    List<LeaveRequest> findByEmployeeIdAndFromDateBetween(String employeeId, LocalDate start, LocalDate end);
    List<LeaveRequest> findByEmployeeIdOrderByAppliedOnDesc(String employeeId);

}
