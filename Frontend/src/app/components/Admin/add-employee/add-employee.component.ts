import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from "@angular/forms";
import { Router, RouterModule, RouterOutlet } from "@angular/router";
import { AdminService } from "../../../services/admin.service";
import { HttpClientModule } from "@angular/common/http";
 
@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ReactiveFormsModule,HttpClientModule,RouterModule],
  templateUrl: './add-employee.component.html',
  styleUrl: './add-employee.component.css'
})
export class AddEmployeeComponent {
 employeeForm: FormGroup;
  isSubmitting = false;
 
  // Dropdown roles — you can add more as needed
  roles = ['Employee', 'Manager', 'Admin'];
 
  constructor(
    private fb: FormBuilder,
    private adminService: AdminService,
    private router: Router
  ) {
    // ✅ Form fields based on your interface
    this.employeeForm = this.fb.group({
      employeeId: [''], // optional field
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      role: ['', Validators.required],
      phone: [''] // optional field
    });
  }
 
    // ✅ Close button handler — redirects to admin info page
  goToAdminInfo() {
    this.router.navigate(['/admin-dashboard']); // <-- Adjust route if your dashboard path differs
  }
 
 
 
  onSubmit() {
    if (this.employeeForm.valid) {
      this.isSubmitting = true;
 
      this.adminService.addEmployee(this.employeeForm.value).subscribe({
        next: (response: any) => {
          alert('✅ Employee added successfully!');
          console.log('Response:', response);
          this.employeeForm.reset();
          this.isSubmitting = false;
        },
        error: (err: any) => {
          console.error('❌ Error adding employee:', err);
          alert('Failed to add employee.');
          this.isSubmitting = false;
        }
      });
    } else {
      alert('⚠️ Please fill all required fields (Name, Email, Role).');
    }
  }
}
 