package com.erp.Employee.enitites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "leave_balance")
public class LeaveBalance {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeId;

    @Column(name = "casual_leave")
    private double casual;

    @Column(name = "sick_leave")
    private double sick;

    @Column(name = "earned_leave")
    private double earned;

    @Column(name = "comp_off")
    private double compOff;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public double getCasual() {
		return casual;
	}

	public void setCasual(double casual) {
		this.casual = casual;
	}

	public double getSick() {
		return sick;
	}

	public void setSick(double sick) {
		this.sick = sick;
	}

	public double getEarned() {
		return earned;
	}

	public void setEarned(double earned) {
		this.earned = earned;
	}

	public double getCompOff() {
		return compOff;
	}

	public void setCompOff(double d) {
		this.compOff = d;
	}

}
