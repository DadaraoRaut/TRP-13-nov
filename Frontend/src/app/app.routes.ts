import { Routes } from '@angular/router';

// =================== Auth Components ===================
import { LoginComponent } from './components/Auth/login/login.component';

// =================== Guards ===================
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

// =================== Admin Components ===================
import { AdminDashboardComponent } from './components/Admin/admin-dashboard/admin-dashboard.component';
import { AddBillerComponent } from './components/Admin/add-biller/add-biller.component';
import { AddEmployeeComponent } from './components/Admin/add-employee/add-employee.component';
import { AddInventoryComponent } from './components/Admin/add-inventory/add-inventory.component';
import { AddSupplierComponent } from './components/Admin/add-supplier/add-supplier.component';
import { AdminInformationComponent } from './components/Admin/admin-information/admin-information.component';
import { PurchaseOrderComponent } from './components/Admin/purchase-order/purchase-order.component';

// =================== Billing Components ===================
import { BillingComponent } from './components/billing/billing.component';
import { BillingInformationComponent } from './components/billing-dashboard/billing-information/billing-information.component';
import { BillingPanelComponent } from './components/billing-dashboard/billing-panel/billing-panel.component';
import { InvoiceComponent } from './components/billing-dashboard/invoice/invoice.component';

// =================== Employee Components ===================
import { EmployeeDashboardComponent } from './components/employee-dashboard/employee-dashboard.component';
import { EmployeeInformationComponent } from './components/Employee/employee-information/employee-information.component';
import { AttendanceComponent } from './components/Employee/attendance/attendance.component';
import { HolidayCalendarComponent } from './components/Employee/holiday-calendar/holiday-calendar.component';
import { LeavesComponent } from './components/Employee/leaves/leaves.component';
import { SalaryComponent } from './components/Employee/salary/salary.component';

// =================== Supplier Components ===================
import { SupplierPanelsComponent } from './components/supplier-panels/supplier-panels.component';
import { SupplierListComponent } from './components/supplier-panel/supplier-list/supplier-list.component';
import { OrderListComponent } from './components/supplier-panel/order-list/order-list.component';
import { SupplierItemFormComponent } from './components/supplier-panel/supplier-item-form/supplier-item-form.component';

export const routes: Routes = [
  // -------- Default --------
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // -------- Login --------
  { path: 'login', component: LoginComponent },

  // -------- Admin Section --------
  {
    path: 'admin-dashboard',
    component: AdminDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'admin' },
    children: [
      { path: '', redirectTo: 'admin-info', pathMatch: 'full' },
      { path: 'admin-info', component: AdminInformationComponent },
      { path: 'add-employee', component: AddEmployeeComponent },
      { path: 'add-supplier', component: AddSupplierComponent },
      { path: 'add-inventory', component: AddInventoryComponent },
      { path: 'add-purchase', component: PurchaseOrderComponent },
      { path: 'add-biller', component: AddBillerComponent },
    ],
  },

  // -------- Employee Section --------
  {
    path: 'employee-dashboard',
    component: EmployeeDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'employee' },
    children: [
      { path: '', redirectTo: 'employee-information', pathMatch: 'full' },
      { path: 'employee-information', component: EmployeeInformationComponent },
      { path: 'attendance', component: AttendanceComponent },
      { path: 'holiday-calendar', component: HolidayCalendarComponent },
      { path: 'leaves', component: LeavesComponent },
      { path: 'salary', component: SalaryComponent },
    ],
  },

  // -------- Billing Section --------
  {
    path: 'billing-dashboard',
    component: BillingComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'biller' },
    children: [
      { path: '', redirectTo: 'billing-information', pathMatch: 'full' },
      { path: 'billing-information', component: BillingInformationComponent },
      { path: 'billing-panel', component: BillingPanelComponent },
      { path: 'invoice', component: InvoiceComponent },
    ],
  },

  // -------- Supplier Section --------
  {
    path: 'supplier-dashboard',
    component: SupplierPanelsComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'supplier' },
    children: [
      { path: '', redirectTo: 'order-list', pathMatch: 'full' },
      { path: 'order-list', component: OrderListComponent },
      { path: 'supplier-item-form', component: SupplierItemFormComponent },
    ],
  },

  // -------- Wildcard / Fallback --------
  { path: '**', redirectTo: 'login' },
];
