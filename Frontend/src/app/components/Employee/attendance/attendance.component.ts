import { Component, OnInit } from '@angular/core';
import { CommonModule, formatDate, Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../../services/employee.service';
import { AttendanceRecord } from './attendance.model';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css']
})
export class AttendanceComponent implements OnInit {
  employeeId: string = '';
  employeeName: string = '';
  attendanceRecords: AttendanceRecord[] = [];
  message: string = '';
  loading: boolean = false;

  constructor(
    private employeeService: EmployeeService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.fetchEmployeeDetails(); // ✅ Load employee details first
  }

  // ✅ Fetch logged-in employee info
  fetchEmployeeDetails(): void {
    this.employeeService.getLoggedEmployee().subscribe({
      next: (data) => {
        console.log('Employee Data:', data);
        this.employeeName = data.name;
        this.employeeId = data.employeeId;

        // ✅ Load attendance for this employee only
        this.loadAttendance();
      },
      error: (err) => {
        console.error('Error fetching employee details:', err);
        this.message = '❌ Failed to load employee details.';
      }
    });
  }

  // 🔙 Go back to previous page
  goBack(): void {
    this.location.back();
  }

  // 🕒 Sign In
  signIn(): void {
    const nowStr = formatDate(new Date(), 'yyyy-MM-ddTHH:mm:ss', 'en-US');
    const record: AttendanceRecord = { employeeId: this.employeeId, signInTime: nowStr };

    this.loading = true;
    this.message = '';

    this.employeeService.signIn(record).subscribe({
      next: () => {
        this.message = `✅ Hello ${this.employeeName || 'Employee'}, signed in successfully!`;
        this.loading = false;
        this.loadAttendance();
      },
      error: (err) => {
        console.error('Sign-in failed:', err);
        this.message = '❌ Sign-in failed. Please try again.';
        this.loading = false;
      }
    });
  }

  // 🕕 Sign Out
  signOut(): void {
    const nowStr = formatDate(new Date(), 'yyyy-MM-ddTHH:mm:ss', 'en-US');
    const record: AttendanceRecord = { employeeId: this.employeeId, signOutTime: nowStr };

    this.loading = true;
    this.message = '';

    this.employeeService.signOut(record).subscribe({
      next: () => {
        this.message = `✅ ${this.employeeName || 'Employee'} signed out successfully!`;
        this.loading = false;
        this.loadAttendance();
      },
      error: (err) => {
        console.error('Sign-out failed:', err);
        this.message = '❌ Sign-out failed. Please try again.';
        this.loading = false;
      }
    });
  }

  // 📋 Load attendance for logged-in employee only
  loadAttendance(): void {
    this.employeeService.getAllAttendance().subscribe({
      next: (data) => {
        // ✅ Filter only the records belonging to the logged-in employee
        this.attendanceRecords = data.filter(
          (record) => record.employeeId === this.employeeId
        );
      },
      error: (err) => {
        console.error('Error loading attendance:', err);
      }
    });
  }
}
