package com.erp.Employee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class LeaveRequestDTO {
    private Long id;
    private String employeeId;
    private String leaveType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Kolkata")
    private LocalDate fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Kolkata")
    private LocalDate toDate;
    private String reason;
    
    private String ccTo;
    private String contactDetails;
    private String status;
    private double remainingBalance;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Kolkata")
    private LocalDate appliedOn;


   


	public LeaveRequestDTO(Long id, String employeeId, String leaveType, LocalDate fromDate, LocalDate toDate,
			String reason, String ccTo, String contactDetails, String status,
			double remainingBalance, LocalDate appliedOn) {
		super();
		this.id = id;
		this.employeeId = employeeId;
		this.leaveType = leaveType;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.reason = reason;
		this.ccTo = ccTo;
		this.contactDetails = contactDetails;
		this.status = status;
		this.remainingBalance = remainingBalance;
		this.appliedOn = appliedOn;
	}


	public LocalDate getAppliedOn() {
		return appliedOn;
	}


	public void setAppliedOn(LocalDate appliedOn) {
		this.appliedOn = appliedOn;
	}





	public double getRemainingBalance() {
		return remainingBalance;
	}


	public void setRemainingBalance(double remainingBalance) {
		this.remainingBalance = remainingBalance;
	}


	public LeaveRequestDTO() {}

   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    


	public String getCcTo() { return ccTo; }
    public void setCcTo(String ccTo) { this.ccTo = ccTo; }

    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
