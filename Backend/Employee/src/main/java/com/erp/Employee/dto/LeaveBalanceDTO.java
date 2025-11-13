package com.erp.Employee.dto;

public class LeaveBalanceDTO {

    private Long id;
    private Long employeeId;
    private int casual;
    private int sick;
    private int earned;
    private int compOff;

    public LeaveBalanceDTO() {}

    public LeaveBalanceDTO(Long id, Long employeeId, int casual, int sick, int earned, int compOff) {
        this.id = id;
        this.employeeId = employeeId;
        this.casual = casual;
        this.sick = sick;
        this.earned = earned;
        this.compOff = compOff;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public int getCasual() { return casual; }
    public void setCasual(int casual) { this.casual = casual; }

    public int getSick() { return sick; }
    public void setSick(int sick) { this.sick = sick; }

    public int getEarned() { return earned; }
    public void setEarned(int earned) { this.earned = earned; }

    public int getCompOff() { return compOff; }
    public void setCompOff(int compOff) { this.compOff = compOff; }
}
