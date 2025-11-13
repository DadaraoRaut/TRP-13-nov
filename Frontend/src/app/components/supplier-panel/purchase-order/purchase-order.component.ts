import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
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
  selector: 'app-purchase-order',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './purchase-order.component.html',
  styleUrls: ['./purchase-order.component.css']
})
export class PurchaseOrderComponent implements OnInit {

  purchaseForm!: FormGroup;

  suppliers: Supplier[] = [];
  products: Product[] = [
    { id: 1, name: 'Tomato', category: 'Veg', price: 50, unit: 'Kg' },
    { id: 2, name: 'Rice', category: 'Groc', price: 60, unit: 'Kg' },
    { id: 3, name: 'Potato', category: 'Veg', price: 30, unit: 'Kg' },
    { id: 4, name: 'Sugar', category: 'Groc', price: 40, unit: 'Kg' }
  ];

  filteredProducts: Product[] = [];
  orderItems: OrderItem[] = [];
  purchaseOrders: PurchaseOrder[] = [];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // ✅ SSR-SAFE → check if window exists before accessing localStorage
    const isBrowser = typeof window !== 'undefined';

    let savedSuppliers: string | null = null;
    let savedOrders: string | null = null;

    if (isBrowser) {
      savedSuppliers = localStorage.getItem('suppliers');
      savedOrders = localStorage.getItem('purchaseOrders');
    }

    this.suppliers = savedSuppliers ? JSON.parse(savedSuppliers) : [
      { id: 1, name: 'Supplier A', mobile: '1234567890', email: 'a@supplier.com', address: 'City A', type: 'Veg' },
      { id: 2, name: 'Supplier B', mobile: '9876543210', email: 'b@supplier.com', address: 'City B', type: 'Groc' },
      { id: 3, name: 'Supplier C', mobile: '5555555555', email: 'c@supplier.com', address: 'City C', type: 'Veg' }
    ];

    this.purchaseOrders = savedOrders ? JSON.parse(savedOrders) : [];

    // ✅ Form initialization
    this.purchaseForm = this.fb.group({
      supplierId: ['', Validators.required],
      productId: ['', Validators.required],
      purchaseQuantity: [null, [Validators.required, Validators.min(1)]],
      reference: ['']
    });

    this.filteredProducts = [...this.products];

    // ✅ Auto filter products when supplier changes
    this.purchaseForm.get('supplierId')?.valueChanges.subscribe(value => {
      const supplier = this.suppliers.find(s => s.id === +value);
      this.filteredProducts = supplier
        ? this.products.filter(p => p.category === supplier.type)
        : this.products;
      this.purchaseForm.patchValue({ productId: '' });
    });
  }

  addItem(): void {
    const productId = +this.purchaseForm.value.productId;
    const quantity = +this.purchaseForm.value.purchaseQuantity;

    if (!productId || !quantity) {
      alert('Please select product and quantity');
      return;
    }

    const product = this.filteredProducts.find(p => p.id === productId);
    if (product) {
      const existing = this.orderItems.find(i => i.product.id === product.id);
      if (existing) {
        existing.purchaseQuantity += quantity;
        existing.totalAmount = existing.purchaseQuantity * product.price;
      } else {
        this.orderItems.push({ product, purchaseQuantity: quantity, totalAmount: quantity * product.price });
      }
    }
  }

  removeItem(index: number) {
    this.orderItems.splice(index, 1);
  }

  getTotalAmount(): number {
    return this.orderItems.reduce((sum, i) => sum + i.totalAmount, 0);
  }

  submitOrder(): void {
    if (!this.purchaseForm.value.supplierId || this.orderItems.length === 0) return;

    const order: PurchaseOrder = {
      id: this.purchaseOrders.length + 1,
      supplierId: +this.purchaseForm.value.supplierId,
      items: this.orderItems,
      reference: this.purchaseForm.value.reference,
      totalAmount: this.getTotalAmount(),
      date: new Date()
    };

    this.purchaseOrders.push(order);

    // ✅ SSR-SAFE localStorage save
    if (typeof window !== 'undefined') {
      localStorage.setItem('purchaseOrders', JSON.stringify(this.purchaseOrders));
    }

    alert('Order submitted successfully!');

    this.orderItems = [];
    this.purchaseForm.reset({
      supplierId: '',
      productId: '',
      purchaseQuantity: null,
      reference: ''
    });
    this.filteredProducts = [...this.products];
  }

  generatePDF(): void {
    if (this.orderItems.length === 0) return;

    const supplier = this.suppliers.find(s => s.id === +this.purchaseForm.value.supplierId);
    const doc = new jsPDF();
    const pageWidth = doc.internal.pageSize.getWidth();

    doc.setFontSize(12);
    doc.setFont('helvetica', 'bold');
    const headerText = 'Purchase Order';
    const textWidth = doc.getTextWidth(headerText);
    doc.text(headerText, (pageWidth - textWidth) / 2, 16);

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.text(`Supplier: ${supplier?.name || '-'}`, 14, 22);
    doc.text(`Product Type: ${supplier?.type || '-'}`, 14, 28);
    doc.text(`Reference: ${this.purchaseForm.value.reference || '-'}`, 14, 34);
    doc.text(`Date: ${new Date().toLocaleDateString()}`, 14, 40);

    const addressText = `Address: ${supplier?.address || '-'}`;
    const splitAddress = doc.splitTextToSize(addressText, 80);
    doc.text(splitAddress, pageWidth - 14 - 80, 22);

    const tableData = this.orderItems.map(item => [
      item.product.name,
      item.product.category,
      item.product.unit,
      item.purchaseQuantity,
      item.product.price,
      item.totalAmount
    ]);

    autoTable(doc, {
      head: [['Product','Category','Unit','Qty','Price','Total']],
      body: tableData,
      startY: 50,
      styles: { fontSize: 9 }
    });

    const finalY = (doc as any).lastAutoTable.finalY + 10;
    doc.text(`Total Amount: ${this.getTotalAmount()}`, 14, finalY);

    const sigY = finalY + 20;
    const sigWidth = 50;
    doc.text('Prepared by:', 14, sigY);
    doc.text('____________________', 14, sigY + 6);

    doc.text('Delivered by:', pageWidth / 2 - sigWidth / 2, sigY);
    doc.text('____________________', pageWidth / 2 - sigWidth / 2, sigY + 6);

    doc.text('Received by:', pageWidth - 14 - sigWidth, sigY);
    doc.text('____________________', pageWidth - 14 - sigWidth, sigY + 6);

    doc.save('purchase_order.pdf');
  }
}
