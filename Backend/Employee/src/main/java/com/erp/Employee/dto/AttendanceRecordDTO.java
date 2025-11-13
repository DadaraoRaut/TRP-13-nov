package com.erp.Employee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceRecordDTO {

    private String employeeId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy hh:mm a", timezone = "Asia/Kolkata")
    private LocalDateTime firstIn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy hh:mm a", timezone = "Asia/Kolkata")
    private LocalDateTime lastOut;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime signInTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime signOutTime;
    private String status;

    private String totalWorkHrs;
    private String breakHrs;
    private String actualWorkHrs;
    private String shortfallHrs;
    private String excessHrs;
    private String lateIn;
    private String earlyOut;
    private String workHrs;
    private String shift;
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getWorkHrs() {
		return workHrs;
	}



	public void setWorkHrs(String workHrs) {
		this.workHrs = workHrs;
	}



	public String getShift() {
		return shift;
	}



	public void setShift(String shift) {
		this.shift = shift;
	}







    public List<AttendanceRecordDTO> getDailyRecords() {
		return dailyRecords;
	}



	public void setDailyRecords(List<AttendanceRecordDTO> dailyRecords) {
		this.dailyRecords = dailyRecords;
	}



	public String getLateIn() {
		return lateIn;
	}



	public void setLateIn(String lateIn) {
		this.lateIn = lateIn;
	}



	public String getEarlyOut() {
		return earlyOut;
	}



	public void setEarlyOut(String earlyOut) {
		this.earlyOut = earlyOut;
	}



	// ✅ No-args constructor
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime session1Start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime session1End;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime session2Start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime session2End;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalDateTime shiftStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalDateTime shiftEnd;
    
  

    // Nested daily record list
    private List<AttendanceRecordDTO> dailyRecords;
 


	


	public LocalDateTime getSession1Start() {
		return session1Start;
	}



	public void setSession1Start(LocalDateTime session1Start) {
		this.session1Start = session1Start;
	}



	public LocalDateTime getSession1End() {
		return session1End;
	}



	public void setSession1End(LocalDateTime session1End) {
		this.session1End = session1End;
	}



	public LocalDateTime getSession2Start() {
		return session2Start;
	}



	public void setSession2Start(LocalDateTime session2Start) {
		this.session2Start = session2Start;
	}



	public LocalDateTime getSession2End() {
		return session2End;
	}



	public void setSession2End(LocalDateTime session2End) {
		this.session2End = session2End;
	}



	public AttendanceRecordDTO() {}

   

    public AttendanceRecordDTO(String employeeId, LocalDate date, LocalDateTime firstIn, LocalDateTime lastOut,
			LocalDateTime signInTime, LocalDateTime signOutTime, String status, String totalWorkHrs, String breakHrs,
			String actualWorkHrs, String shortfallHrs, String excessHrs) {
		super();
		this.employeeId = employeeId;
		this.date = date;
		this.firstIn = firstIn;
		this.lastOut = lastOut;
		this.signInTime = signInTime;
		this.signOutTime = signOutTime;
		this.status = status;
		this.totalWorkHrs = totalWorkHrs;
		this.breakHrs = breakHrs;
		this.actualWorkHrs = actualWorkHrs;
		this.shortfallHrs = shortfallHrs;
		this.excessHrs = excessHrs;
	}



	// Getters & Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDateTime getFirstIn() { return firstIn; }
    public void setFirstIn(LocalDateTime firstIn) { this.firstIn = firstIn; }
    public LocalDateTime getLastOut() { return lastOut; }
    public void setLastOut(LocalDateTime lastOut) { this.lastOut = lastOut; }
    public LocalDateTime getSignInTime() { return signInTime; }
    public void setSignInTime(LocalDateTime signInTime) { this.signInTime = signInTime; }
    public LocalDateTime getSignOutTime() { return signOutTime; }
    public void setSignOutTime(LocalDateTime signOutTime) { this.signOutTime = signOutTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

	public String getTotalWorkHrs() {
		return totalWorkHrs;
	}

	public void setTotalWorkHrs(String totalWorkHrs) {
		this.totalWorkHrs = totalWorkHrs;
	}

	public String getBreakHrs() {
		return breakHrs;
	}

	public void setBreakHrs(String breakHrs) {
		this.breakHrs = breakHrs;
	}

	public String getActualWorkHrs() {
		return actualWorkHrs;
	}

	public void setActualWorkHrs(String actualWorkHrs) {
		this.actualWorkHrs = actualWorkHrs;
	}

	public String getShortfallHrs() {
		return shortfallHrs;
	}

	public void setShortfallHrs(String shortfallHrs) {
		this.shortfallHrs = shortfallHrs;
	}

	public String getExcessHrs() {
		return excessHrs;
	}

	public void setExcessHrs(String excessHrs) {
		this.excessHrs = excessHrs;
	}



	public LocalDateTime getShiftStart() {
		return shiftStart;
	}



	public void setShiftStart(LocalDateTime shiftStart) {
		this.shiftStart = shiftStart;
	}



	public LocalDateTime getShiftEnd() {
		return shiftEnd;
	}



	public void setShiftEnd(LocalDateTime shiftEnd) {
		this.shiftEnd = shiftEnd;
	}


  
}
