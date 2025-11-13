package com.erp.Employee.repository;

import com.erp.Employee.enitites.AttendanceRecord;
import com.erp.Employee.enitites.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, String> {
//    Optional<AttendanceRecord> findByEmployeeId(String employeeId);
    List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

//    Optional<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date);

//    List<AttendanceRecord> records = repository.findAll();
    List<AttendanceRecord> findAll();

    List<AttendanceRecord> findByEmployeeIdAndDateBetween(String employeeId, LocalDate start, LocalDate end);

    List<AttendanceRecord> findByEmployeeId(String employeeId);

}
