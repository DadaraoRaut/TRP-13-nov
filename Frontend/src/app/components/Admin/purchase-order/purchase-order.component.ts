import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { AdminService, PurchaseOrder, Supplier, Product } from '../../../services/admin.service';
 
@Component({
  selector: 'app-purchase-order',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './purchase-order.component.html',
  styleUrls: ['./purchase-order.component.css']
})
export class PurchaseOrderComponent implements OnInit {
 
  purchaseOrders: PurchaseOrder[] = [];
  suppliers: Supplier[] = [];
  products: Product[] = [];
  selectedProduct: Product | null = null;
 
  quantityDisplay: string | number = 1;
 
  order: PurchaseOrder = {
    orderId: '',
    supplierId: '',
    totalAmount: 0,
    paymentMode: '',
    paymentStatus: '',
    orderStatus: 'PENDING'
  };
 
  constructor(private adminService: AdminService, private router: Router) {}
 
  ngOnInit(): void {
    this.loadAllData();
  }
 
  // ✅ Load All (Orders, Suppliers, Products)
  loadAllData(): void {
    this.adminService.getOrders().subscribe({
      next: (data) => this.purchaseOrders = data || [],
      error: (err) => console.error('Error loading orders', err)
    });
 
    this.adminService.getAllSuppliers().subscribe({
      next: (data) => this.suppliers = data || [],
      error: (err) => console.error('Error loading suppliers', err)
    });
 
    this.adminService.getProducts().subscribe({
      next: (data) => this.products = data || [],
      error: (err) => console.error('Error loading products', err)
    });
  }
 
  // ✅ Go Back Button
  goToAdminInfo(): void {
    this.router.navigate(['/admin-dashboard']);
  }
 
  // ✅ Quantity Handlers
  onQuantityFocus(): void {
    if (this.quantityDisplay === 1 || this.quantityDisplay === '1') {
      this.quantityDisplay = '';
    }
  }
 
  onQuantityBlur(): void {
    if (this.quantityDisplay === '' || this.quantityDisplay === null) {
      this.quantityDisplay = 1;
    }
    this.calculateTotal();
  }
 
  // ✅ Total Calculation
  onProductSelect(): void {
    this.calculateTotal();
  }
 
  calculateTotal(): void {
    const qty = Number(this.quantityDisplay) || 0;
    const price = this.selectedProduct ? this.selectedProduct.pricePerUnit : 0;
    this.order.totalAmount = qty * price;
  }
 
  // ✅ Form Reset
  resetForm(): void {
    this.selectedProduct = null;
    this.quantityDisplay = 1;
    this.order = {
      orderId: '',
      supplierId: '',
      totalAmount: 0,
      paymentMode: '',
      paymentStatus: '',
      orderStatus: 'PENDING'
    };
  }
 
  // ✅ Submit Order
  submitOrder(): void {
    const newOrder: PurchaseOrder = {
      ...this.order,
      orderId: `PO-${Date.now()}`,
      orderDate: new Date().toISOString()
    };
 
    this.adminService.addPurchaseOrder(newOrder).subscribe({
      next: (saved) => {
        alert('✅ Purchase Order created successfully!');
        this.purchaseOrders.push(saved);
        this.resetForm();
      },
      error: (err) => {
        console.error('Error creating order', err);
        alert('❌ Failed to create order');
      }
    });
  }
 
  // ✅ Update Status
  updateStatus(orderId: string, newStatus: string): void {
    this.adminService.updateOrderStatus(orderId, newStatus).subscribe({
      next: () => {
        alert(`Order status updated to ${newStatus}`);
        this.loadAllData();
      },
      error: (err) => {
        console.error('Error updating order status', err);
        alert('Failed to update status');
      }
    });
  }
}