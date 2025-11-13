import { Component, OnInit } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { BillingPanelService } from '../../../services/billing-panel.service';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

interface Product {
  id: string;
  name: string;
  category: string;
  barcode: string;
  unit: string;
  availableQuantity: number;
  price: number;
}

interface BillItem {
  id: string;
  name: string;
  category: string;
  barcode: string;
  unit: string;
  quantity: number;
  price: number;
  taxPercent: number;
  taxAmount: number;
  total: number;
}

@Component({
  selector: 'app-billing-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './billing-panel.component.html',
  styleUrls: ['./billing-panel.component.css']
})
export class BillingPanelComponent implements OnInit {
  billNumber: string | null = null;
  billDateTime: Date = new Date();

  products: Product[] = [];
  selectedProduct: Product | null = null;
  billItems: BillItem[] = [];

  purchaseQty: number = 0;
  taxPercent: number = 0;
  calculatedTotal: number = 0;
  totalAmount: number = 0;

  isEditing = false;
  editingIndex: number | null = null;

  paymentMethod: string = 'cash';
  upiId: string = '';
  cardNumber: string = '';
  expiryDate: string = '';
  cvv: string = '';

  constructor(private BillingService: BillingPanelService, private router: Router) {}

  ngOnInit(): void {
    this.generateBillInfo();
    this.loadProducts();
  }

  generateBillInfo() {
    this.billNumber = 'BILL-' + Date.now();
    this.billDateTime = new Date();
  }

  loadProducts() {
    this.BillingService.getAllProducts().subscribe({
      next: (res: any[]) => {
        this.products = res.map(item => ({
          id: item.id,
          name: item.productName,
          category: item.category,
          barcode: item.productId,
          unit: item.unit,
          availableQuantity: item.quantity,
          price: item.pricePerUnit
        }));
        console.log('✅ Products loaded:', this.products);
      },
      error: (err) => {
        console.error('❌ Error loading products:', err);
        alert('Failed to load products from backend.');
      }
    });
  }

  onProductSelect(event: any) {
    const id = event.target.value;
    this.selectedProduct = this.products.find(p => p.id === id) || null;
    this.purchaseQty = 0;
    this.taxPercent = 0;
    this.calculatedTotal = 0;
  }

  autoCalculateTotal() {
    if (this.selectedProduct && this.purchaseQty > 0) {
      const subtotal = this.selectedProduct.price * this.purchaseQty;
      const taxAmt = subtotal * (this.taxPercent / 100);
      this.calculatedTotal = subtotal + taxAmt;
    }
  }

  addProductToBill() {
    if (!this.selectedProduct) return alert('⚠️ Please select a product.');
    if (this.purchaseQty <= 0) return alert('⚠️ Enter a valid quantity.');
    if (this.purchaseQty > this.selectedProduct.availableQuantity)
      return alert('⚠️ Quantity exceeds available stock.');

    const subtotal = this.selectedProduct.price * this.purchaseQty;
    const taxAmount = subtotal * (this.taxPercent / 100);

    const newItem: BillItem = {
      id: this.selectedProduct.id,
      name: this.selectedProduct.name,
      category: this.selectedProduct.category,
      barcode: this.selectedProduct.barcode,
      unit: this.selectedProduct.unit,
      quantity: this.purchaseQty,
      price: this.selectedProduct.price,
      taxPercent: this.taxPercent,
      taxAmount,
      total: subtotal
    };

    this.billItems.push(newItem);
    this.updateGrandTotal();
    this.selectedProduct = null;
    this.purchaseQty = 0;
    this.taxPercent = 0;
    this.calculatedTotal = 0;
  }

  editProduct(index: number) {
    this.isEditing = true;
    this.editingIndex = index;
    const item = this.billItems[index];
    this.selectedProduct = {
      id: item.id,
      name: item.name,
      category: item.category,
      barcode: item.barcode,
      unit: item.unit,
      availableQuantity: 0,
      price: item.price
    };
    this.purchaseQty = item.quantity;
    this.taxPercent = item.taxPercent;
    this.autoCalculateTotal();
  }

  updateProduct() {
    if (this.editingIndex === null || !this.selectedProduct) return;

    const subtotal = this.selectedProduct.price * this.purchaseQty;
    const taxAmount = subtotal * (this.taxPercent / 100);

    this.billItems[this.editingIndex] = {
      id: this.selectedProduct.id,
      name: this.selectedProduct.name,
      category: this.selectedProduct.category,
      barcode: this.selectedProduct.barcode,
      unit: this.selectedProduct.unit,
      quantity: this.purchaseQty,
      price: this.selectedProduct.price,
      taxPercent: this.taxPercent,
      taxAmount,
      total: subtotal
    };

    this.isEditing = false;
    this.editingIndex = null;
    this.selectedProduct = null;
    this.purchaseQty = 0;
    this.taxPercent = 0;
    this.updateGrandTotal();
  }

  cancelEdit() {
    this.isEditing = false;
    this.editingIndex = null;
    this.selectedProduct = null;
  }

  removeItem(index: number) {
    this.billItems.splice(index, 1);
    this.updateGrandTotal();
  }

  updateGrandTotal() {
    this.totalAmount = this.billItems.reduce((sum, item) => sum + item.total + item.taxAmount, 0);
  }

  saveDraft() {
    const draft = {
      billNumber: this.billNumber,
      billDate: this.billDateTime,
      items: this.billItems,
      totalAmount: this.totalAmount,
      status: 'DRAFT'
    };
    sessionStorage.setItem('draftBill', JSON.stringify(draft));
    alert('📝 Bill saved as draft!');
  }

  formatCardNumber(event: any) {
    let input = event.target.value.replace(/\s/g, '').replace(/(\d{4})/g, '$1 ').trim();
    event.target.value = input;
    this.cardNumber = input;
  }

 processPayment() {
  if (this.billItems.length === 0) {
    alert('⚠️ Please add at least one product!');
    return;
  }

  const items = this.billItems.map(item => ({
    itemId: Number(item.id),
    name: item.name,
    category: item.category,
    barcode: item.barcode,
    pricePerUnit: item.price,
    quantity: item.quantity
  }));

  const paymentDetail =
    this.paymentMethod === 'upi'
      ? this.upiId
      : this.paymentMethod === 'card'
      ? this.cardNumber
      : 'Cash Payment';

  const billData = {
    items,
    paymentMethod: this.paymentMethod.toUpperCase(), // matches enum PaymentMethod
    paymentDetail,
    billedBy: 101, // or current logged-in user ID
    gstPercentage: this.taxPercent // if fixed, set to overall GST rate
  };

  console.log('🧾 Sending bill data:', billData);

  this.BillingService.createBill(billData).subscribe({
    next: (res) => {
      alert('✅ Bill created successfully!');
      this.generateInvoice();
      this.billItems = [];
      this.totalAmount = 0;
    },
    error: (err) => {
      console.error('❌ Payment failed:', err);
      alert('Error creating bill: ' + (err.error?.message || 'Invalid request.'));
    }
  });
}


  generateInvoice() {
    const latestBill = {
      billNumber: this.billNumber,
      dateTime: this.billDateTime,
      billedBy: 'BILLER101',
      paymentMethod: this.paymentMethod,
      paymentStatus: 'PAID',
      subtotal: this.billItems.reduce((sum, i) => sum + i.total, 0),
      totalAmount: this.totalAmount,
      items: this.billItems.map(item => ({
        itemName: item.name,
        quantity: item.quantity,
        pricePerUnit: item.price,
        taxPercent: item.taxPercent,
        taxAmount: item.taxAmount,
        totalPrice: item.total + item.taxAmount
      }))
    };

    sessionStorage.setItem('latestInvoice', JSON.stringify(latestBill));
    this.router.navigate(['/billing/invoice']);
  }

  /** ✅ FIXED PDF Export */
  downloadBillPdf() {
    if (this.billItems.length === 0) return alert('No items to export.');

    const doc = new jsPDF();
    doc.setFontSize(14);
    doc.text(`Invoice - ${this.billNumber}`, 10, 10);
    doc.text(`Date: ${formatDate(this.billDateTime, 'dd/MM/yyyy HH:mm', 'en-IN')}`, 10, 18);

    autoTable(doc, {
      startY: 25,
      head: [['Item', 'Qty', 'Price', 'GST%', 'Total']],
      body: this.billItems.map(i => [
        i.name,
        i.quantity,
        i.price.toFixed(2),
        i.taxPercent + '%',
        (i.total + i.taxAmount).toFixed(2)
      ])
    });

    // ✅ Safe access for TypeScript
    const finalY = (doc as any).lastAutoTable?.finalY || 40;
    doc.text(`Grand Total: ₹${this.totalAmount.toFixed(2)}`, 10, finalY + 10);
    doc.save(`Invoice_${this.billNumber}.pdf`);
  }

  closeBilling() {
    this.router.navigate(['/login']);
  }
}
