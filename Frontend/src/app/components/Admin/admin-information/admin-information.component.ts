import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
 
@Component({
  selector: 'app-admin-information',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-information.component.html',
  styleUrls: ['./admin-information.component.css']
})
export class AdminInformationComponent {
  // 🔹 Admin basic info
  admin = {
    name: 'Kishan Kumar',
    email: 'admin@erp.com',
    phone: '+91 9876543210',
    role: 'System Administrator',
    department: 'Management',
    joinDate: '2022-03-15'
  };
 
  // 🔹 4 main module stats
  employeeStats = { total: 25, pendingPayroll: 3, onLeave: 2 };
  supplierStats = { total: 15, pendingOrders: 4, active: 12 };
  inventoryStats = { totalItems: 120, lowStock: 10, outOfStock: 3 };
  purchaseStats = { totalOrders: 60, completed: 52, pending: 8 };
 
  // 🔹 Recent admin actions
  adminActivity = [
    'Added new supplier "FreshMart"',
    'Approved purchase order #PO-2025',
    'Updated stock levels for "Amul Butter"',
    'Processed payroll for March 2025',
    'Created new employee profile for "Ravi Kumar"'
  ];
}
 
 