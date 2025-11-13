import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const expectedRole = route.data['role'];   // e.g. { path: 'admin', canActivate:[RoleGuard], data:{ role:'admin' } }
    const userRole = localStorage.getItem('role');

    if (userRole && expectedRole && userRole.toLowerCase() === expectedRole.toLowerCase()) {
      return true;  // ✅ Correct role
    } else {
      alert('Access denied! You do not have permission for this page.');
      // Recommended: use UrlTree instead of navigate()
      return this.router.parseUrl('/login');
    }
  }
}
