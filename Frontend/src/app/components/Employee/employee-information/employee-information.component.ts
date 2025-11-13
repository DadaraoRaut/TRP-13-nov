import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-employee-information',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './employee-information.component.html',
  styleUrls: ['./employee-information.component.css']
})
export class EmployeeInformationComponent {

  // 🔹 Basic employee info
  employee = {
    name: 'Kunal John',
    email: 'Quantum@erpsupermarket.com',
    phone: '+91 9876543210',
    department: 'Sales & Billing',
    designation: 'Billing Executive',
    joinDate: '2024-06-15'
  };

  // 🔹 Quick summary from 4 employee modules
  moduleStats = {
    attendance: 'Present for 22 days this month',
    holiday: 'Next holiday: Diwali (Nov 7)',
    salary: '₹35,000 credited on Nov 1, 2025',
    leaves: '2 leaves remaining this month'
  };

  // 🔹 Recent employee activities
  employeeActivity: string[] = [
    'Checked in at 9:00 AM today',
    'Processed 45 customer bills',
    'Reviewed inventory stock levels',
    'Generated monthly sales report',
    'Updated leave balance details'
  ];
}
