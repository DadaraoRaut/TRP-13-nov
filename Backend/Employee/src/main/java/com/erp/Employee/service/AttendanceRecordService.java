package com.erp.Employee.service;

import com.erp.Employee.dto.AttendanceRecordDTO;

import java.time.YearMonth;
import java.util.List;

public interface AttendanceRecordService {
//    void delete(String employeeId);
    List<AttendanceRecordDTO> getAll();
    List<AttendanceRecordDTO> getDailyAttendance(String employeeId, YearMonth month);
	AttendanceRecordDTO markAttendance(AttendanceRecordDTO dto);
	AttendanceRecordDTO getMonthlyAttendance(String employeeId, int year, int month);

}
