package com.erp.Employee.service;


import java.util.List;

import com.erp.Employee.dto.LeaveRequestDTO;
import com.erp.Employee.enitites.Employee;
import com.erp.Employee.enitites.LeaveBalance;
import com.erp.Employee.enitites.LeaveRequest;

public interface LeaveService {

	 LeaveRequestDTO applyLeave(LeaveRequestDTO dto);

	    LeaveRequestDTO cancelLeave(Long id); // ✅ Cancel by leave ID

	    void cancelLeavesByEmployee(String employeeId); // ✅ Cancel all pending leaves by employee ID

	    List<LeaveRequestDTO> getLeavesByEmployee(String employeeId);

	    List<LeaveRequestDTO> getPendingLeaves(String employeeId);

	    List<LeaveRequestDTO> getLeaveHistory(String employeeId);

	    List<Employee> getAllEmployees();

	    String cancelLeaveByEmployeeId(String employeeId);
	    void grantCompOff(String employeeId, int days);
        LeaveBalance getLeaveBalance(String employeeId);

}
