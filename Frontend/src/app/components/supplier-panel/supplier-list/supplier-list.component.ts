import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

interface Supplier {
  id: number;
  name: string;
  mobile: string;
  email: string;
  address: string;
  type: 'Veg' | 'Groc';
}

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './supplier-list.component.html',
  styleUrls: ['./supplier-list.component.css']
})
export class SupplierListComponent implements OnInit {

  suppliers: Supplier[] = [];
  supplierForm: FormGroup;
  editing: boolean = false;
  editingId: number | null = null;

  constructor(private fb: FormBuilder) {
    this.supplierForm = this.fb.group({
      name: ['', Validators.required],
      mobile: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      email: ['', [Validators.required, Validators.email]],
      address: [''],
      type: ['Veg', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadSuppliers();
  }

  // ---------------- LOAD SUPPLIERS ----------------
  loadSuppliers() {
    if (typeof window === 'undefined' || !window.localStorage) {
      // SSR or non-browser environment
      console.warn('localStorage not available (SSR mode)');
      this.suppliers = [];
      return;
    }

    const storedSuppliers = localStorage.getItem('suppliers');
    if (storedSuppliers) {
      this.suppliers = JSON.parse(storedSuppliers);
    } else {
      // Default demo data
      this.suppliers = [
        { id: 1, name: 'Supplier A', mobile: '1234567890', email: 'a@supplier.com', address: 'City A', type: 'Veg' },
        { id: 2, name: 'Supplier B', mobile: '9876543210', email: 'b@supplier.com', address: 'City B', type: 'Groc' },
        { id: 3, name: 'Supplier C', mobile: '5555555555', email: 'c@supplier.com', address: 'City C', type: 'Veg' }
      ];
      localStorage.setItem('suppliers', JSON.stringify(this.suppliers));
    }
  }

  // ---------------- ADD OR UPDATE SUPPLIER ----------------
  submitForm() {
    if (this.supplierForm.invalid) return;

    const supplier: Supplier = {
      id: this.editing
        ? (this.editingId as number)
        : (this.suppliers.length ? Math.max(...this.suppliers.map(s => s.id)) + 1 : 1),
      ...this.supplierForm.value
    };

    if (this.editing) {
      const index = this.suppliers.findIndex(s => s.id === this.editingId);
      if (index !== -1) {
        this.suppliers[index] = supplier;
      }
      this.editing = false;
      this.editingId = null;
    } else {
      this.suppliers.push(supplier);
    }

    // ✅ Save only in browser
    if (typeof window !== 'undefined' && window.localStorage) {
      localStorage.setItem('suppliers', JSON.stringify(this.suppliers));
    }

    this.supplierForm.reset({ type: 'Veg' });
  }

  // ---------------- EDIT SUPPLIER ----------------
  editSupplier(supplier: Supplier) {
    this.editing = true;
    this.editingId = supplier.id;
    this.supplierForm.setValue({
      name: supplier.name,
      mobile: supplier.mobile,
      email: supplier.email,
      address: supplier.address,
      type: supplier.type
    });
  }

  // ---------------- DELETE SUPPLIER ----------------
  deleteSupplier(id: number) {
    if (confirm('Are you sure to delete this supplier?')) {
      this.suppliers = this.suppliers.filter(s => s.id !== id);

      if (typeof window !== 'undefined' && window.localStorage) {
        localStorage.setItem('suppliers', JSON.stringify(this.suppliers));
      }
    }
  }

  // ---------------- CANCEL EDIT ----------------
  cancelEdit() {
    this.editing = false;
    this.editingId = null;
    this.supplierForm.reset({ type: 'Veg' });
  }

  // ---------------- FORM VALIDATION HELPER ----------------
  isSubmitDisabled(): boolean {
    return this.supplierForm.invalid;
  }
}
