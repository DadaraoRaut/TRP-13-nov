import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): boolean | UrlTree {
    const token = localStorage.getItem('token');

    if (token) {
      return true; // ✅ User logged in
    } else {
      alert('Please login first!');
      // navigate ki jagah UrlTree return karo — best practice in guards
      return this.router.parseUrl('/login');
    }
  }
}
