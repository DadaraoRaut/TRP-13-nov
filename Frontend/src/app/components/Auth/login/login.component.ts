import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { LoginService } from '../../../services/login.service';
import { CommonModule } from '@angular/common';
// import { LoginService } from '../../services/login.service'; // ✅ adjust path
 
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule,RouterOutlet,RouterModule,ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']

})
export class LoginComponent {
  loginForm: FormGroup;
 
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private loginService: LoginService
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      role: ['', Validators.required]
    });


        // ✅ Prevent going back after logout
    if (!localStorage.getItem('token')) {
      history.pushState(null, '', window.location.href);
      window.onpopstate = function () {
        history.go(1);
      };
    }

  }

  
 
  onSubmit() {
    const { username, password, role } = this.loginForm.value;
 
    this.loginService.login(username, password, role).subscribe({
      next: (res) => {
        alert('Login successful!');
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
 
        // Redirect by role
        switch (res.role.toLowerCase()) {
          case 'admin':
            this.router.navigate(['/admin-dashboard']);
            break;
          case 'employee':
            this.router.navigate(['/employee-dashboard']);
            break;
          case 'supplier':
            this.router.navigate(['/supplier-panels']);
            break;
          case 'biller':
            this.router.navigate(['/billing']);
            break;
          default:
            alert('Invalid role');
        }
      },
      error: (err) => {
        alert(err.error.message || 'Login failed!');
      }
    });
  }
}
 
 