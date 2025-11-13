package com.erp.Employee.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalaryDTO {

    private String employeeId;
    private String employeeName;
    private String department;
    private String designation;
    private String salaryMonth;
    private double basic;
    private double hra;
    private double conveyance;
    private double medical;
    private double specialAllowance;
    private double pf;
    private double esi;
    private double tax;
    private double grossSalary;
    private double totalDeductions;
    private double netSalary;

    private String companyName;
    private String companyLocation;
    private String bankName;
    private int effectiveWorkDays;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy ", timezone = "Asia/Kolkata")
    private LocalDate generatedAt;
    private String token; // JWT token from request header
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyLocation() {
		return companyLocation;
	}
	public void setCompanyLocation(String companyLocation) {
		this.companyLocation = companyLocation;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public int getEffectiveWorkDays() {
		return effectiveWorkDays;
	}
	public void setEffectiveWorkDays(int effectiveWorkDays) {
		this.effectiveWorkDays = effectiveWorkDays;
	}
	

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(String salaryMonth) { this.salaryMonth = salaryMonth; }

    public double getBasic() { return basic; }
    public void setBasic(double basic) { this.basic = basic; }

    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }

    public double getConveyance() { return conveyance; }
    public void setConveyance(double conveyance) { this.conveyance = conveyance; }

    public double getMedical() { return medical; }
    public void setMedical(double medical) { this.medical = medical; }

    public double getSpecialAllowance() { return specialAllowance; }
    public void setSpecialAllowance(double specialAllowance) { this.specialAllowance = specialAllowance; }

    public double getPf() { return pf; }
    public void setPf(double pf) { this.pf = pf; }

    public double getEsi() { return esi; }
    public void setEsi(double esi) { this.esi = esi; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }

    public double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(double totalDeductions) { this.totalDeductions = totalDeductions; }

    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }

    public LocalDate getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDate generatedAt) { this.generatedAt = generatedAt; }
}
