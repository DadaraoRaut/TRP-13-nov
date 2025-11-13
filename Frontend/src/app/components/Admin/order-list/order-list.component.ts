import { CommonModule, isPlatformBrowser } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
 
interface Supplier {
  id: number;
  name: string;
  mobile: string;
  email: string;
  address: string;
  type: 'Veg' | 'Groc';
}
 
interface Product {
  id: number;
  name: string;
  category: 'Veg' | 'Groc';
  price: number;
  unit: string;
}
 
interface OrderItem {
  product: Product;
  purchaseQuantity: number;
  totalAmount: number;
}
 
interface PurchaseOrder {
  id: number;
  supplierId: number;
  items: OrderItem[];
  reference?: string;
  totalAmount: number;
  date: Date;
}
 
@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, HttpClientModule],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.css'
})
export class OrderListComponent implements OnInit {
suppliers: Supplier[] = [];
  orders: PurchaseOrder[] = [];
  isBrowser = false;
 
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }
 
  ngOnInit(): void {
    if (this.isBrowser) {
      this.loadSuppliers();
      this.loadOrders();
    }
  }
 
  // ---------------- LOAD SUPPLIERS ----------------
  loadSuppliers() {
    const savedSuppliers = localStorage.getItem('suppliers');
    if (savedSuppliers) {
      this.suppliers = JSON.parse(savedSuppliers);
    } else {
      this.suppliers = [
        { id: 1, name: 'Supplier A', mobile: '1234567890', email: 'a@supplier.com', address: 'City A', type: 'Veg' },
        { id: 2, name: 'Supplier B', mobile: '9876543210', email: 'b@supplier.com', address: 'City B', type: 'Groc' },
        { id: 3, name: 'Supplier C', mobile: '5555555555', email: 'c@supplier.com', address: 'City C', type: 'Veg' }
      ];
      localStorage.setItem('suppliers', JSON.stringify(this.suppliers));
    }
  }
 
  // ---------------- LOAD ORDERS ----------------
  loadOrders() {
    const savedOrders = localStorage.getItem('purchaseOrders');
    if (savedOrders) {
      this.orders = JSON.parse(savedOrders).map((o: any) => ({
        ...o,
        date: new Date(o.date)
      }));
    } else {
      this.orders = [];
      localStorage.setItem('purchaseOrders', JSON.stringify(this.orders));
    }
  }
 
  // ---------------- HELPERS ----------------
  getSupplierName(id: number): string {
    const supplier = this.suppliers.find(s => s.id === id);
    return supplier ? supplier.name : 'Unknown';
  }
 
  getOrderTotal(order: PurchaseOrder): number {
    return order.items.reduce((sum, item) => sum + item.totalAmount, 0);
  }
 
  // ---------------- DELETE ORDER ----------------
  deleteOrder(id: number) {
    if (!this.isBrowser) return;
    if (confirm('Are you sure to delete this order?')) {
      this.orders = this.orders.filter(o => o.id !== id);
      localStorage.setItem('purchaseOrders', JSON.stringify(this.orders));
    }
  }
 
  // ---------------- GENERATE PDF ----------------
  generatePDF(order: PurchaseOrder) {
    const supplier = this.suppliers.find(s => s.id === order.supplierId);
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();
 
    doc.setFontSize(10);
    doc.text(`Supplier: ${supplier?.name || 'Unknown'}`, 14, 16);
    doc.text(`Product: ${supplier?.type || '-'}`, 14, 22);
    doc.text(`Reference: ${order.reference || '-'}`, 14, 28);
    doc.text(`Date: ${order.date.toLocaleDateString()}`, 14, 34);
 
    const addressText = `Address: ${supplier?.address || '-'}`;
    const splitAddress = doc.splitTextToSize(addressText, 80);
    doc.text(splitAddress, pageWidth - 14 - 80, 16);
 
    const tableData = order.items.map((item: OrderItem) => [
      item.product.name,
      item.product.category,
      item.product.unit,
      item.purchaseQuantity,
      item.product.price,
      item.totalAmount
    ]);
 
    autoTable(doc, {
      head: [['Product', 'Category', 'Unit', 'Qty', 'Price', 'Total']],
      body: tableData,
      startY: 45,
      styles: { fontSize: 9 }
    });
 
    const finalY = (doc as any).lastAutoTable.finalY + 10;
    doc.text(`Total Amount: ${this.getOrderTotal(order)}`, 14, finalY);
 
    const sigY = finalY + 20;
    const sigWidth = 50;
    doc.text('Prepared by:', 14, sigY);
    doc.text('____________________', 14, sigY + 6);
    doc.text('Delivered by:', pageWidth / 2 - sigWidth / 2, sigY);
    doc.text('____________________', pageWidth / 2 - sigWidth / 2, sigY + 6);
    doc.text('Received by:', pageWidth - 14 - sigWidth, sigY);
    doc.text('____________________', pageWidth - 14 - sigWidth, sigY + 6);
 
    doc.save(`purchase_order_${order.id}.pdf`);
  }
 
}
