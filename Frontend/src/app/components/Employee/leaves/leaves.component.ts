import { CommonModule, Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { EmployeeService } from '../../../services/employee.service';
 
@Component({
  selector: 'app-leaves',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterModule],
  templateUrl: './leaves.component.html',
  styleUrls: ['./leaves.component.css']
})
export class LeavesComponent implements OnInit {
  activeTab: string = 'apply';
  employeeId: string = '';
 
  // Form fields
  leaveType: string = '';
  fromDate: string = '';
  toDate: string = '';
  reason: string = '';
  ccTo: string = '';
  contactDetails: string = '';
 
  // Data holders
  leaveBalances: any = null;
  leavesList: any[] = [];
  pendingLeaves: any[] = [];
  historyLeaves: any[] = [];
 
  loading: boolean = false;
  message: string = '';
 
  constructor(
    private location: Location,
    private employeeService: EmployeeService
  ) {}
 
  ngOnInit() {
    // 👇 Load logged-in employee ID (replace this with your actual login key)
    this.employeeId = localStorage.getItem('employeeId') || '';
 
  
 
    // Automatically load default data (optional)
    this.fetchLeaveBalance();
  }
 
  goBack() {
    this.location.back();
  }
 
  setTab(tab: string) {
    this.activeTab = tab;
    this.message = '';
 
    switch (tab) {
      case 'balance':
        this.fetchLeaveBalance();
        break;
      case 'cancel':
        this.fetchAllLeaves();
        break;
      case 'pending':
        this.fetchPendingLeaves();
        break;
      case 'history':
        this.fetchLeaveHistory();
        break;
    }
  }
 
  applyLeave() {
    if (!this.leaveType || !this.fromDate || !this.toDate || !this.reason) {
      this.message = 'Please fill all required fields';
      return;
    }
 
    const payload = {
      employeeId: this.employeeId,
      leaveType: this.leaveType,
      fromDate: this.fromDate,
      toDate: this.toDate,
      reason: this.reason,
      ccTo: this.ccTo,
      contactDetails: this.contactDetails
    };
 
    this.loading = true;
    this.employeeService.applyLeave(payload).subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Leave applied successfully!';
        this.leaveType = this.fromDate = this.toDate = this.reason = this.ccTo = this.contactDetails = '';
        this.fetchLeaveBalance();
      },
      error: (err) => {
        this.loading = false;
        this.message = 'Error applying leave: ' + (err.error?.message || err.message);
      }
    });
  }
 
  fetchLeaveBalance() {
 
    this.loading = true;
    this.employeeService.getLeaveBalance(this.employeeId).subscribe({
      next: (res) => {
        this.loading = false;
        this.leaveBalances = res;
      },
      error: (err) => {
        this.loading = false;
        this.message = 'Error fetching leave balance: ' + (err.error?.message || err.message);
      }
    });
  }
 
  fetchAllLeaves() {
 
    this.loading = true;
    this.employeeService.getAllLeaves(this.employeeId).subscribe({
      next: (res: any[]) => {
        this.loading = false;
        this.leavesList = res;
      },
      error: (err) => {
        this.loading = false;
        this.message = 'Error fetching leaves: ' + err.message;
      }
    });
  }
 
  fetchPendingLeaves() {
 
    this.loading = true;
    this.employeeService.getPendingLeaves(this.employeeId).subscribe({
      next: (res: any[]) => {
        this.loading = false;
        this.pendingLeaves = res;
      },
      error: (err) => {
        this.loading = false;
        this.message = 'Error fetching pending leaves: ' + err.message;
      }
    });
  }
 
  fetchLeaveHistory() {
 
    this.loading = true;
    this.employeeService.getLeaveHistory(this.employeeId).subscribe({
      next: (res: any[]) => {
        this.loading = false;
        this.historyLeaves = res;
      },
      error: (err) => {
        this.loading = false;
        this.message = 'Error fetching history: ' + err.message;
      }
    });
  }
 
 
 
cancelLeave(leaveId: string) {
  if (!confirm('Are you sure you want to cancel this leave?')) return;
 
  this.loading = true;
  this.employeeService.cancelLeave(leaveId).subscribe({
    next: (res: any) => {
      this.loading = false;
      this.message = 'Leave cancelled successfully ✅';
 
      // Remove the canceled leave from the cancel tab list instantly
      this.leavesList = this.leavesList.filter(l => l.id !== leaveId);
 
      // Optional: Refresh canceled list from backend to keep in sync
      this.fetchAllLeaves();
    },
    error: (err) => {
      this.loading = false;
 
      // Handle specific backend messages safely
      if (err.error && typeof err.error === 'string' && err.error.includes('already cancelled')) {
        this.message = 'This leave was already cancelled.';
      } else {
        console.error('Error cancelling leave:', err);
        this.message = 'Something went wrong while cancelling the leave.';
      }
    }
  });
}
 
  }
 
 