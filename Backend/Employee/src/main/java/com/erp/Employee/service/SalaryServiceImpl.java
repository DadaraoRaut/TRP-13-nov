package com.erp.Employee.service;

import com.erp.Employee.dto.EmployeeDTO;
import com.erp.Employee.dto.SalaryDTO;
import com.erp.Employee.enitites.Salary;
import com.erp.Employee.exception.ResourceNotFoundException;
import com.erp.Employee.repository.EmployeeRepository;
import com.erp.Employee.repository.SalaryRepository;
import com.erp.Employee.security.JwtUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepo;
    private final EmployeeRepository employeeRepo;
    private final JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${admin-service.base-url}")
    private String adminServiceUrl;

    @Autowired
    public SalaryServiceImpl(SalaryRepository salaryRepo, EmployeeRepository employeeRepo, JwtUtil jwtUtil) {
        this.salaryRepo = salaryRepo;
        this.employeeRepo = employeeRepo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public SalaryDTO generateSalary(SalaryDTO dto) throws RestClientException {

        // 1️⃣ Extract JWT token
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("JWT token is missing in request");
        }

        // 2️⃣ Setup headers for Admin service
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 3️⃣ Call Admin Service for employee info
        String url = adminServiceUrl + dto.getEmployeeId();
        ResponseEntity<EmployeeDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, EmployeeDTO.class);

        EmployeeDTO employee = response.getBody();
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found: " + dto.getEmployeeId());
        }

        // 🧩 4️⃣ Validate that salary month is not in the future
        if (dto.getSalaryMonth() == null || dto.getSalaryMonth().isEmpty()) {
            throw new IllegalArgumentException("Salary month cannot be empty (expected format: YYYY-MM)");
        }

        try {
            java.time.YearMonth requestedMonth = java.time.YearMonth.parse(dto.getSalaryMonth());
            java.time.YearMonth currentMonth = java.time.YearMonth.now();

            if (requestedMonth.isAfter(currentMonth)) {
                throw new IllegalArgumentException("Salary cannot be generated for a future month: " + dto.getSalaryMonth());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid salary month format. Expected format: YYYY-MM");
        }
        // 4️⃣ Prevent duplicate salary
        salaryRepo.findByEmployeeIdAndSalaryMonth(dto.getEmployeeId(), dto.getSalaryMonth())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Salary for employee "
                            + dto.getEmployeeId() + " for month "
                            + dto.getSalaryMonth() + " already exists");
                });

        // 5️⃣ Salary calculation
        double hra = dto.getHra() > 0 ? dto.getHra() : dto.getBasic() * 0.10;
        double conveyance = dto.getConveyance() > 0 ? dto.getConveyance() : dto.getBasic() * 0.05;
        double medical = dto.getMedical() > 0 ? dto.getMedical() : dto.getBasic() * 0.03;
        double specialAllowance = dto.getSpecialAllowance() > 0 ? dto.getSpecialAllowance() : dto.getBasic() * 0.07;

        double pf = dto.getPf() > 0 ? dto.getPf() : dto.getBasic() * 0.12;
        double esi = dto.getEsi() > 0 ? dto.getEsi() : dto.getBasic() * 0.0075;
        double tax = dto.getTax() > 0 ? dto.getTax() : dto.getBasic() * 0.10;

        double gross = dto.getBasic() + hra + conveyance + medical + specialAllowance;
        double totalDeductions = pf + esi + tax;
        double net = gross - totalDeductions;

        String bankName = "State Bank of India";
        String companyName = "QuantumsofTechnologies Private Limited, Pune";
        String companyLocation = "Office No. 6010, 6th Floor, A, Solitaire Business Hub, near Phoenix Mall, Viman Nagar, Pune, Maharashtra";
        int effectiveWorkDays = 30;
        String salaryMonth = dto.getSalaryMonth() != null ? dto.getSalaryMonth() : LocalDate.now().toString().substring(0, 7);

        String department = dto.getDepartment() != null ? dto.getDepartment()
                : (employee.getDepartment() != null ? employee.getDepartment() : "Not Assigned");
        String designation = dto.getDesignation() != null ? dto.getDesignation()
                : (employee.getDesignation() != null ? employee.getDesignation() : "Not Assigned");

        // 6️⃣ Save Salary
        Salary salary = new Salary(
                employee.getEmployeeId(),
//                employee.getFirstName() + " " + employee.getLastName(),
                employee.getName(),
                department,
                designation,
                dto.getSalaryMonth(),
                dto.getBasic(),
                hra,
                conveyance,
                medical,
                specialAllowance,
                pf,
                esi,
                tax,
                gross,
                totalDeductions,
                net,
                bankName,
                companyName,
                effectiveWorkDays,
                LocalDate.now(),
                companyLocation,
                employee.getAddress()
        );

        salaryRepo.save(salary);

//        dto.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
        dto.getEmployeeName();
        dto.setDepartment(department);
        dto.setDesignation(designation);
        dto.setSalaryMonth(salaryMonth);
        dto.setHra(hra);
        dto.setConveyance(conveyance);
        dto.setMedical(medical);
        dto.setSpecialAllowance(specialAllowance);
        dto.setPf(pf);
        dto.setEsi(esi);
        dto.setTax(tax);
        dto.setGrossSalary(gross);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(net);
        dto.setBankName(bankName);
        dto.setCompanyName(companyName);
        dto.setCompanyLocation(companyLocation);
        dto.setEffectiveWorkDays(effectiveWorkDays);
        dto.setGeneratedAt(LocalDate.now());
        dto.setAddress(employee.getAddress());


        return dto;
    }

    // ✅ Secure salary retrieval
    @Override
    public SalaryDTO getSalary(String employeeId, String month) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        String loggedInEmpId = getEmployeeIdFromToken(token);

        if (!loggedInEmpId.equals(employeeId)) {
            throw new SecurityException("Access Denied: You can only view your own salary details.");
        }

        Salary salary = salaryRepo.findByEmployeeIdAndSalaryMonth(employeeId, month)
                .orElseThrow(() -> new ResourceNotFoundException("Salary not found for " + employeeId + " in " + month));
        return convertToDTO(salary);
    }

    @Override
    public List<SalaryDTO> getSalariesByEmployee(String employeeId) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        String loggedInEmpId = getEmployeeIdFromToken(token);

        if (!loggedInEmpId.equals(employeeId)) {
            throw new SecurityException("Access Denied: You can only view your own salary records.");
        }

        return salaryRepo.findByEmployeeId(employeeId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<SalaryDTO> getAllSalaries() {
        return salaryRepo.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public String getEmployeeIdFromToken(String token) {
        return jwtUtil.extractEmployeeId(token.replace("Bearer ", ""));
    }

    @Override
    public ByteArrayInputStream generatePayslipPdf(String employeeId, String month) {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        String loggedInEmpId = getEmployeeIdFromToken(token);

        if (!loggedInEmpId.equals(employeeId)) {
            throw new SecurityException("Access Denied: You can only generate your own payslip.");
        }

        SalaryDTO salary = getSalary(employeeId, month);
        if (salary == null) {
            throw new ResourceNotFoundException("Salary not found for " + employeeId + " month: " + month);
        }
        return generatePayslipPdfFromDTO(salary);
    }

    private SalaryDTO convertToDTO(Salary s) {
        SalaryDTO dto = new SalaryDTO();
        dto.setEmployeeId(s.getEmployeeId());
        dto.setEmployeeName(s.getEmployeeName());
        dto.setDepartment(s.getDepartment());
        dto.setDesignation(s.getDesignation());
        dto.setSalaryMonth(s.getSalaryMonth());
        dto.setBasic(s.getBasic());
        dto.setHra(s.getHra());
        dto.setConveyance(s.getConveyance());
        dto.setMedical(s.getMedical());
        dto.setSpecialAllowance(s.getSpecialAllowance());
        dto.setPf(s.getPf());
        dto.setEsi(s.getEsi());
        dto.setTax(s.getTax());
        dto.setGrossSalary(s.getGrossSalary());
        dto.setTotalDeductions(s.getTotalDeductions());
        dto.setNetSalary(s.getNetSalary());
        dto.setGeneratedAt(s.getGeneratedAt());
        dto.setAddress(s.getAddress());
        return dto;
    }

    @Override
    public ByteArrayInputStream generatePayslipPdfFromDTO(SalaryDTO salary) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            Paragraph header = new Paragraph("QuantumSoft Technologies Private Limited", titleFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            Paragraph address = new Paragraph(
                    "Office No. 6010, 6th Floor, A, Solitaire Business Hub,\nnear Phoenix Mall, Viman Nagar, Pune, Maharashtra - 411014",
                    bodyFont);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            document.add(new Paragraph("\nPayslip for the month of " + salary.getSalaryMonth(), boldFont));
            document.add(new Paragraph("\nPrint Date: " + LocalDate.now(), bodyFont));
            document.add(new Paragraph("\n"));

            PdfPTable empTable = new PdfPTable(2);
            empTable.setWidthPercentage(100);
            empTable.addCell("Name");
            empTable.addCell(salary.getEmployeeName());

            empTable.addCell("Employee ID");
            empTable.addCell(String.valueOf(salary.getEmployeeId()));

            empTable.addCell("Department");
            empTable.addCell(salary.getDepartment());

            empTable.addCell("Designation");
            empTable.addCell(salary.getDesignation());

            empTable.addCell("Location");
            empTable.addCell(salary.getAddress());

            empTable.addCell("Effective Work Days");
            empTable.addCell("30");

            empTable.addCell("Bank Name");
            empTable.addCell("State Bank of India");

            empTable.addCell("Generated On");
            empTable.addCell(String.valueOf(salary.getGeneratedAt()));

            document.add(empTable);

            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            PdfPTable earnings = new PdfPTable(2);
            earnings.addCell(new Paragraph("Earnings", boldFont));
            earnings.addCell("");
            earnings.addCell("BASIC"); earnings.addCell(String.format("%.2f", salary.getBasic()));
            earnings.addCell("HRA"); earnings.addCell(String.format("%.2f", salary.getHra()));
            earnings.addCell("Conveyance"); earnings.addCell(String.format("%.2f", salary.getConveyance()));
            earnings.addCell("Medical"); earnings.addCell(String.format("%.2f", salary.getMedical()));
            earnings.addCell("Special Allowance"); earnings.addCell(String.format("%.2f", salary.getSpecialAllowance()));
            earnings.addCell("Gross Salary"); earnings.addCell(String.format("%.2f", salary.getGrossSalary()));

            PdfPTable deductions = new PdfPTable(2);
            deductions.addCell(new Paragraph("Deductions", boldFont));
            deductions.addCell("");
            deductions.addCell("PF"); deductions.addCell(String.format("%.2f", salary.getPf()));
            deductions.addCell("ESI"); deductions.addCell(String.format("%.2f", salary.getEsi()));
            deductions.addCell("TAX"); deductions.addCell(String.format("%.2f", salary.getTax()));
            deductions.addCell("Total Deductions"); deductions.addCell(String.format("%.2f", salary.getTotalDeductions()));

            table.addCell(earnings);
            table.addCell(deductions);
            document.add(table);

            document.add(new Paragraph("\nTotal Earnings: INR " + String.format("%.2f", salary.getGrossSalary())
                    + "    Total Deductions: INR " + String.format("%.2f", salary.getTotalDeductions()), boldFont));
            document.add(new Paragraph("\nNet Pay for the Month: INR " + String.format("%.2f", salary.getNetSalary()), boldFont));
            document.add(new Paragraph("\n(" + convertAmountToWords((int) salary.getNetSalary()) + " Only)", bodyFont));
            document.add(new Paragraph("\n\nThis is a system generated payslip and does not require signature.", bodyFont));
//            Paragraph sign = new Paragraph("\n\n" + salary.getEmployeeName(), boldFont);
//            sign.setAlignment(Element.ALIGN_RIGHT);
//            document.add(sign);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String convertAmountToWords(double amount) {
        int rupees = (int) amount;
        int paise = (int) Math.round((amount - rupees) * 100);
        String words = convertIntegerToWords(rupees) + " Rupees";
        if (paise > 0) {
            words += " and " + convertIntegerToWords(paise) + " Paise";
        }
        return words + " Only";
    }

    private String convertIntegerToWords(int number) {
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
                "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        if (number == 0) return "Zero";
        if (number < 20) return units[number];
        if (number < 100)
            return tens[number / 10] + (number % 10 != 0 ? " " + units[number % 10] : "");
        if (number < 1000)
            return units[number / 100] + " Hundred" + (number % 100 != 0 ? " " + convertIntegerToWords(number % 100) : "");
        if (number < 100000)
            return convertIntegerToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + convertIntegerToWords(number % 1000) : "");
        if (number < 10000000)
            return convertIntegerToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + convertIntegerToWords(number % 100000) : "");
        return convertIntegerToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + convertIntegerToWords(number % 10000000) : "");
    }
}
