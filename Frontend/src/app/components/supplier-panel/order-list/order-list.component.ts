import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  SupplierPanelService,
  PurchaseOrderResponse,
  ProductDetail
} from '../../../services/supplier-panel.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {
  orders: PurchaseOrderResponse[] = [];
  isBrowser = false;
  isLoading = false;
  errorMessage = '';

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private supplierService: SupplierPanelService
  ) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  ngOnInit(): void {
    if (this.isBrowser) {
      this.loadOrders();
    }
  }

  /** =================== FETCH ORDERS =================== **/
  loadOrders(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.supplierService.getOrders().subscribe({
      next: (orders) => {
        console.log('✅ Orders fetched:', orders);
        this.orders = Array.isArray(orders)
  ? orders.map((o) => ({
      ...o,
      products: Array.isArray(o.products) ? o.products.map(p => ({
        ...p,
        quantity: p.quantity ?? 0,
        pricePerUnit: p.pricePerUnit ?? 0
      })) : [],
      orderDate: o.orderDate ? new Date(o.orderDate) : new Date()
    }))
  : [];

        this.isLoading = false;
      },
      error: (err) => {
        console.error('❌ Error fetching orders:', err);
        this.errorMessage = 'Failed to load supplier orders.';
        this.isLoading = false;
      }
    });
  }

  /** =================== ORDER ITEMS =================== **/
  getOrderItems(order: PurchaseOrderResponse): ProductDetail[] {
    return Array.isArray(order.products) ? order.products : [];
  }

  /** =================== TOTAL CALCULATION =================== **/
  getOrderTotal(order: PurchaseOrderResponse): number {
    if (order.totalAmount) return order.totalAmount;
    const items = this.getOrderItems(order);
    return items.reduce((sum, item) => {
      const qty = item.quantity ?? 0;
      const price = item.pricePerUnit ?? 0;
      return sum + qty * price;
    }, 0);
  }

  /** =================== ORDER STATUS CHECK =================== **/
  isPending(order: PurchaseOrderResponse): boolean {
    return order.orderStatus === 'PENDING';
  }

  /** =================== ACCEPT / REJECT ORDER =================== **/
  respondToOrder(order: PurchaseOrderResponse, action: 'accept' | 'reject'): void {
    if (!this.isBrowser) return;

    this.supplierService.respondToOrder(order.orderId, action).subscribe({
      next: (updatedOrder) => {
        // update entire order with backend response
        Object.assign(order, updatedOrder);
        alert(`✅ Order ${action}ed successfully!`);
      },
      error: (err) => {
        console.error('❌ Failed to respond to order:', err);
        alert('Something went wrong while updating order status.');
      }
    });
  }

  /** =================== EXPORT PDF =================== **/
  generatePDF(order: PurchaseOrderResponse): void {
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();

    // ======= HEADER =======
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(16);
    doc.text('ERP Supermarket Pvt. Ltd.', pageWidth / 2, 15, { align: 'center' });

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.text('123, Main Street, Pune, India - 400001', pageWidth / 2, 21, { align: 'center' });
    doc.text('Phone: +91 98765 43210 | Email: contact@erpsupermarket.in', pageWidth / 2, 26, {
      align: 'center'
    });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(13);
    doc.text('Purchase Order Summary', pageWidth / 2, 38, { align: 'center' });
    doc.setLineWidth(0.5);
    doc.line(14, 40, pageWidth - 14, 40);

    // ======= ORDER DETAILS =======
    const formattedDate = order.orderDate ? new Date(order.orderDate).toLocaleString() : 'N/A';
    const orderDetails = [
      ['Order ID', order.orderId || 'N/A'],
      ['Supplier ID', order.supplierId || 'N/A'],
      ['Order Date', formattedDate],
      ['Payment Mode', order.paymentMode || 'N/A'],
      ['Payment Status', order.paymentStatus || 'N/A'],
      ['Order Status', order.orderStatus || 'N/A'],
      ['Total Amount (₹)', this.getOrderTotal(order).toFixed(2)]
    ];

    autoTable(doc, {
      startY: 47,
      head: [['Field', 'Value']],
      body: orderDetails,
      styles: { fontSize: 10, cellPadding: 3, halign: 'left', valign: 'middle' },
      headStyles: { fillColor: [41, 128, 185], textColor: 255, fontStyle: 'bold' },
      alternateRowStyles: { fillColor: [245, 245, 245] },
      theme: 'grid'
    });

    // ======= PRODUCT DETAILS TABLE =======
    const items = this.getOrderItems(order);
    if (items.length > 0) {
      const productBody = items.map((p) => [
        p.productId ?? 'N/A',
        p.productName ?? 'N/A',
        p.productType ?? 'N/A',
        p.unitType ?? 'N/A',
        p.quantity ?? 0,
        p.pricePerUnit ?? 0,
        ((p.quantity ?? 0) * (p.pricePerUnit ?? 0)).toFixed(2)
      ]);

      autoTable(doc, {
        startY: (doc as any).lastAutoTable.finalY + 10,
        head: [['Product ID', 'Name', 'Type', 'Unit', 'Qty', 'Price/Unit', 'Subtotal (₹)']],
        body: productBody,
        styles: { fontSize: 9, cellPadding: 2 },
        headStyles: { fillColor: [52, 73, 94], textColor: 255, fontStyle: 'bold' },
        alternateRowStyles: { fillColor: [248, 248, 248] },
        theme: 'striped'
      });
    }

    // ======= SIGNATURES =======
    const finalY = (doc as any).lastAutoTable?.finalY + 25 || 120;
    const sigWidth = 50;

    doc.setFontSize(10);
    doc.text('Prepared by:', 14, finalY);
    doc.text('____________________', 14, finalY + 6);

    doc.text('Delivered by:', pageWidth / 2 - sigWidth / 2, finalY);
    doc.text('____________________', pageWidth / 2 - sigWidth / 2, finalY + 6);

    doc.text('Received by:', pageWidth - 14 - sigWidth, finalY);
    doc.text('____________________', pageWidth - 14 - sigWidth, finalY + 6);

    // ======= FOOTER =======
    const footerY = finalY + 25;
    doc.setFontSize(9);
    doc.setTextColor(100);
    doc.text('This is a system-generated document from ERP Supermarket.', pageWidth / 2, footerY, {
      align: 'center'
    });
    doc.text('Thank you for doing business with us!', pageWidth / 2, footerY + 5, { align: 'center' });

    // ======= SAVE =======
    doc.save(`purchase_order_${order.orderId}.pdf`);
  }
}
