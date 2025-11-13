import { Component } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { EmployeeService } from '../../../services/employee.service';
 
@Component({
  selector: 'app-salary',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './salary.component.html',
  styleUrls: ['./salary.component.css']
})
export class SalaryComponent {
  employeeId: string = '';
  salaryMonth: string = '';
  basic: number | null = null;
 
  loading: boolean = false;
  downloading: boolean = false;
  requested: boolean = false;
  message: string = '';
 
  constructor(
    private location: Location,
    private employeeService: EmployeeService
  ) {}
 
  goBack(): void {
    this.location.back();
  }
 ngOnInit() {
    // 👇 Load logged-in employee ID (replace this with your actual login key)
    this.employeeId = localStorage.getItem('employeeId') || '';
 
    if (!this.employeeId) {
      this.message = 'Employee ID not found. Please log in again.';
      return;
    }
 
    // Automatically load default data (optional)
    this.requestSalarySlip();
  }
 
  // ✅ Request salary slip generation
  requestSalarySlip(): void {
    if (  !this.salaryMonth || this.basic === null) {
      this.message = 'Please fill all fields';
      return;
    }
 
    this.loading = true;
    this.message = '';
 
    const payload = {
      employeeId: this.employeeId,
      salaryMonth: this.salaryMonth,
      basic: this.basic
    };
 
    this.employeeService.generateSalary(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.requested = true;
        this.message = 'Salary slip generated successfully. You can now download it.';
      },
      error: (err) => {
        console.error('Error generating salary slip:', err);
        this.loading = false;
        this.message = 'Failed to generate salary slip.';
      }
    });
  }
 
  // ✅ Download generated payslip PDF
  downloadPayslip(): void {
    if (!this.salaryMonth) {
      this.message = 'Please select a salary month first';
      return;
    }
 
    this.downloading = true;
    this.message = '';
 
    this.employeeService.downloadPayslip(this.salaryMonth).subscribe({
      next: (blob) => {
        this.downloading = false;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `payslip-${this.salaryMonth}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.message = 'Payslip downloaded successfully.';
      },
      error: (err) => {
        console.error('Error downloading payslip:', err);
        this.downloading = false;
        this.message = 'Failed to download payslip.';
      }
    });
  }
}
 
 