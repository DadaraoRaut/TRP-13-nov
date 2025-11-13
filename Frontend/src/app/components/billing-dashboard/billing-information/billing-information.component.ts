import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-billing-information',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './billing-information.component.html',
  styleUrls: ['./billing-information.component.css']
})
export class BillingInformationComponent {

  // 🔹 Basic Billing Info
  billingInfo = {
    billerName: 'Pallavi Jarande',
    billerId: 'BILL-2025-0098',
    department: 'Sales & Billing',
    terminalId: 'POS-T01',
    totalBillsToday: 128,
    totalRevenue: '₹2,45,600'
  };

  // 🔹 Quick Summary (from related billing modules)
  summaryStats = {
    todaySales: '₹2,45,600 (128 bills)',
    monthlySales: '₹12,85,000 till date',
    topProduct: 'Fortune Oil 1L',
    discountGiven: '₹15,200 offered this month'
  };

  // 🔹 Recent Billing Activities
  recentActivity: string[] = [
    'Generated bill #INV-10234 for ₹1,250',
    'Applied festive discount on Groceries',
    'Processed 10 online UPI transactions',
    'Refunded bill #INV-10201 (Damaged item)',
    'Closed POS shift for the day'
  ];
}
