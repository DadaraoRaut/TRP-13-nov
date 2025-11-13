import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, HostListener } from '@angular/core';
import { RouterModule, RouterOutlet, Router } from '@angular/router'; // ✅ import Router

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HttpClientModule, RouterModule],
  templateUrl: './employee-dashboard.component.html',
  styleUrls: ['./employee-dashboard.component.css']
})
export class EmployeeDashboardComponent {
  // Sidebar States
  isCollapsed = false;
  isMobile = false;
  isMobileSidebarOpen = false;

  constructor(private router: Router) {   // ✅ inject Router
    this.checkScreenSize();
  }

  // ✅ Detect screen resize for responsive behavior
  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth <= 768;
    if (!this.isMobile) {
      this.isMobileSidebarOpen = false;
    }
  }

  // ✅ Sidebar toggle logic
  toggleSidebar() {
    if (this.isMobile) {
      this.isMobileSidebarOpen = !this.isMobileSidebarOpen;
    } else {
      this.isCollapsed = !this.isCollapsed;
    }
  }

  closeMobileSidebar() {
    this.isMobileSidebarOpen = false;
  }

  // ✅ Logout method: redirect to Admin Dashboard
  logout() {
    // remove employee data (but keep admin session)
    localStorage.removeItem('employeeId');
    localStorage.removeItem('employeeName');

    // redirect to admin dashboard page
    this.router.navigate(['/admin-dashboard']);
  }
}
