import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule,Location } from '@angular/common';
import { SupplierPanelService, SupplierItem } from '../../../services/supplier-panel.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-supplier-item-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './supplier-item-form.component.html',
  styleUrls: ['./supplier-item-form.component.css']
})
export class SupplierItemFormComponent implements OnInit {

  itemForm!: FormGroup;
  items: SupplierItem[] = [];
  editMode: boolean = false;
  currentItemId: number | null = null;

  productTypes = ['VEGETABLE', 'FRUIT', 'GROCERY'];
  unitTypes = ['GM', 'KG', 'LITER', 'QUANTITY'];
  filteredUnits: string[] = [];

  constructor(private fb: FormBuilder, private supplierService: SupplierPanelService,private location: Location) {}

  ngOnInit(): void {
    this.itemForm = this.fb.group({
      productName: ['', Validators.required],
      productType: ['', Validators.required],
      unitType: ['', [Validators.required, this.unitValidator.bind(this)]],
      quantity: [null, [Validators.required, Validators.min(0.01)]],
      pricePerUnit: [null, [Validators.required, Validators.min(0.01)]]
    });

    this.loadItems();

    // Update filtered units when productType changes
    this.itemForm.get('productType')?.valueChanges.subscribe(type => {
      this.filterUnits(type);
      this.itemForm.get('unitType')?.updateValueAndValidity();
    });
  }

  // 🔙 Navigate back to previous page
  goBack(): void {
    this.location.back(); // ✅ works reliably
  }

  // Custom validator to check unitType compatibility with productType
  unitValidator(control: AbstractControl): ValidationErrors | null {
    const productType = this.itemForm?.get('productType')?.value;
    const unit = control.value;
    if (!productType || !unit) return null;

    if ((productType === 'VEGETABLE' || productType === 'FRUIT') && !(unit === 'KG' || unit === 'GM')) {
      return { invalidUnit: 'Vegetable/Fruit must be in KG or GM' };
    }
    if (productType === 'GROCERY' && !(unit === 'KG' || unit === 'LITER' || unit === 'QUANTITY')) {
      return { invalidUnit: 'Grocery must be in KG, LITER, or QUANTITY' };
    }
    return null;
  }

  filterUnits(type: string) {
    if (type === 'VEGETABLE' || type === 'FRUIT') this.filteredUnits = ['KG', 'GM'];
    else if (type === 'GROCERY') this.filteredUnits = ['KG', 'LITER', 'QUANTITY'];
    else this.filteredUnits = [];

    const currentUnit = this.itemForm.get('unitType')?.value;
    if (!this.filteredUnits.includes(currentUnit)) {
      this.itemForm.get('unitType')?.setValue('');
    }
  }

  loadItems() {
    this.supplierService.getItems().subscribe({
      next: data => this.items = data,
      error: err => console.error('Error loading items', err)
    });
  }

  onSubmit() {
    if (this.itemForm.invalid) {
      const errors = this.getFormErrors();
      alert('Please fix errors:\n' + errors.join('\n'));
      return;
    }

    const itemData: SupplierItem = this.itemForm.value;

    if (this.editMode && this.currentItemId !== null) {
      this.supplierService.updateItem(this.currentItemId, itemData).subscribe({
        next: () => {
          alert('✅ Item updated successfully!');
          this.resetForm();
          this.loadItems();
        },
        error: err => {
          console.error(err);
          alert('❌ Failed to update item: ' + (err.error?.message || 'Server error'));
        }
      });
    } else {
      this.supplierService.addItem(itemData).subscribe({
        next: () => {
          alert('✅ Item added successfully!');
          this.resetForm();
          this.loadItems();
        },
        error: err => {
          console.error(err);
          alert('❌ Failed to add item: ' + (err.error?.message || 'Server error'));
        }
      });
    }
  }

  editItem(item: SupplierItem) {
    this.editMode = true;
this.currentItemId = item.id ?? null;
    this.itemForm.patchValue({ ...item });
    this.filterUnits(item.productType);
  }

  deleteItem(id: number) {
    if (confirm('⚠️ Are you sure you want to delete this item?')) {
      this.supplierService.deleteItem(id).subscribe({
        next: () => {
          alert('🗑️ Item deleted successfully!');
          this.loadItems();
        },
        error: err => {
          console.error(err);
          alert('❌ Failed to delete item: ' + (err.error?.message || 'Server error'));
        }
      });
    }
  }

  resetForm() {
    this.itemForm.reset();
    this.editMode = false;
    this.currentItemId = null;
    this.filteredUnits = [];
  }

  getFormErrors(): string[] {
    const errors: string[] = [];
    Object.keys(this.itemForm.controls).forEach(key => {
      const control = this.itemForm.get(key);
      if (control && control.errors) {
        Object.values(control.errors).forEach(err => {
          if (typeof err === 'string') errors.push(`${key}: ${err}`);
          else errors.push(`${key} is invalid`);
        });
      }
    });
    return errors;
  }
}
