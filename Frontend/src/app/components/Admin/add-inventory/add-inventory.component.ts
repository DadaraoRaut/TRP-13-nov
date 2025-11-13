import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService, Inventory } from '../../../services/admin.service';

@Component({
  selector: 'app-add-inventory',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterModule],
  templateUrl: './add-inventory.component.html',
  styleUrls: ['./add-inventory.component.css']
})
export class AddInventoryComponent {
  inventoryForm: FormGroup;
  isSubmitting = false;
  router: any;

  constructor(private fb: FormBuilder, private adminService: AdminService) {
    this.inventoryForm = this.fb.group({
      productName: ['', Validators.required],
      category: [''],
      quantity: [0, [Validators.required, Validators.min(1)]],
      pricePerUnit: [0, [Validators.required, Validators.min(0)]],
      unit: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.inventoryForm.valid) {
      this.isSubmitting = true;

      const formValue = this.inventoryForm.value;
      const inventoryData: Inventory = {
        ...formValue,
        productId: 'PROD-' + Math.floor(Math.random() * 100000), // ✅ unique ID
        supplierId: localStorage.getItem('supplierId') || 'SUP-DEFAULT' // optional
      };

      console.log('✅ Submitting Inventory Data:', inventoryData);

      this.adminService.addInventory(inventoryData).subscribe({
        next: (response) => {
          console.log('✅ Saved successfully:', response);
          alert('✅ Inventory added successfully!');
          this.inventoryForm.reset();
          this.isSubmitting = false;
        },
        error: (err) => {
          console.error('❌ Error saving inventory:', err);
          alert('❌ Failed to save inventory. Please check console for details.');
          this.isSubmitting = false;
        }
      });
    } else {
      alert('⚠️ Please fill all required fields correctly.');
    }
  }

   goToAdminInfo() {
    this.router.navigate(['/admin-dashboard']); // <-- Adjust route if your dashboard path differs
  }
}
