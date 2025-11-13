import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, HostListener } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HttpClientModule, RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent {
  isCollapsed = false;
  isMobile = false;
  isMobileSidebarOpen = false;

  constructor(private router: Router) {
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

  // ✅ Logout logic
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    sessionStorage.clear();

    // Prevent going back
    history.pushState(null, '', window.location.href);
    window.onpopstate = () => {
      history.go(1);
    };

    this.router.navigate(['/login']);
  }
}
