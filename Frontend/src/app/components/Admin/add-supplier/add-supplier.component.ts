import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AdminService } from '../../../services/admin.service';
import { HttpClientModule } from '@angular/common/http';
 
@Component({
  selector: 'app-add-supplier',
  standalone: true,
  imports: [CommonModule,RouterOutlet,ReactiveFormsModule,HttpClientModule,RouterModule],
  templateUrl: './add-supplier.component.html',
  styleUrls: ['./add-supplier.component.css']
})
export class AddSupplierComponent {
supplierForm: FormGroup;
 
  isSubmitting = false;
 
  constructor(private fb: FormBuilder, private adminService: AdminService,  private router: Router ) {
 
    this.supplierForm = this.fb.group({
 
      name: ['', Validators.required],
 
      email: ['', [Validators.required, Validators.email]],
 
      mobileNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
 
      address: ['', Validators.required],
 
      supplierType: ['GROC', Validators.required],
 
      role: ['SUPPLIER', Validators.required]
 
    });
 
  }
 
   // ✅ Close button handler — redirects to admin info page
  goToAdminInfo() {
    this.router.navigate(['/admin-dashboard']); // <-- Adjust route if your dashboard path differs
  }
 
  onSubmit() {
 
    if (this.supplierForm.valid) {
 
      this.isSubmitting = true;
 
      const supplierData = this.supplierForm.value;
 
      this.adminService.addSupplier(supplierData).subscribe({
 
        next: (response) => {
 
          console.log('Supplier added:', response);
 
          alert('Supplier added successfully!');
 
          this.supplierForm.reset({
 
            supplierType: 'GROC',
 
            role: 'SUPPLIER'
 
          });
 
          this.isSubmitting = false;
 
        },
 
        error: (err) => {
 
          console.error('Error adding supplier:', err);
 
          alert('Failed to add supplier.');
 
          this.isSubmitting = false;
 
        }
 
      });
 
    }
 
  }
}
 
 
 