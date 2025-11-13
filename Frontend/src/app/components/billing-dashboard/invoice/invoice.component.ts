import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { BillingPanelService } from '../../../services/billing-panel.service';

// ✅ TypeScript interfaces
interface InvoiceItem {
  itemName: string;
  quantity: number;
  pricePerUnit: number;
  taxPercent: number;
  taxAmount: number;
  totalPrice: number;
}

interface Invoice {
  billNumber: string;
  dateTime: Date;
  billedBy: string;
  paymentMethod: string;
  paymentDetail?: string;
  paymentStatus: string;
  subtotal: number;
  totalAmount: number;
  items: InvoiceItem[];
}

@Component({
  selector: 'app-invoice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice.component.html',
  styleUrls: ['./invoice.component.css']
})
export class InvoiceComponent implements OnInit {
  invoices: Invoice[] = [];
  selectedInvoice: Invoice | null = null;
  billSummary: any;

  constructor(private router: Router, private BillingService: BillingPanelService) { }


   ngOnInit(): void {
  // Load all invoices from backend
  this.BillingService.getAllBills().subscribe({
    next: (res: any[]) => {
      this.invoices = res.map(bill => ({
        billNumber: bill.billNumber || 'N/A',
        dateTime: new Date(bill.billDate || bill.createdAt || new Date()),
        billedBy: bill.billedBy || 'Admin',
        paymentMethod: bill.paymentMethod || 'CASH',
        paymentDetail: bill.paymentDetail || '',
        paymentStatus: bill.paymentStatus || 'PAID',
        subtotal: bill.subtotal || 0,
        totalAmount: bill.totalAmount || 0,
        items: bill.items?.map((i: any) => ({
          itemName: i.name,
          quantity: i.quantity,
          pricePerUnit: i.pricePerUnit,
          taxPercent: i.taxPercent || 0,
          taxAmount: i.taxAmount || 0,
          totalPrice: i.total || 0
        })) || []
      }));
      console.log('✅ Loaded all invoices:', this.invoices);
    },
    error: (err) => {
      console.error('❌ Failed to load invoices:', err);
      alert('Failed to fetch invoices from server.');
    }
  });
   }


  viewInvoice(invoice: Invoice): void {
    this.selectedInvoice = invoice;
  }

  closeInvoice(): void {
    this.selectedInvoice = null;
  }

  getTotalGST(invoice: Invoice | null): number {
    if (!invoice?.items) return 0;
    return invoice.items.reduce((sum, item: InvoiceItem) => sum + item.taxAmount, 0);
  }

 printInvoice(): void {
  if (!this.selectedInvoice) return;

  const invoice = this.selectedInvoice;
  const printContents = `
    <div style="font-family: Arial, sans-serif; width: 700px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px;">
      <div style="text-align: center; border-bottom: 2px solid #007bff; padding-bottom: 10px;">
        <img src="assets/logo.png" alt="Company Logo" style="width: 100px; height: auto; margin-bottom: 5px;">
        <h2 style="margin: 5px 0; color: #007bff;">SuperMart ERP Billing</h2>
        <p style="font-size: 13px; color: #555;">
          123 Market Road, Pune, Maharashtra — 411001<br>
          Phone: +91 98765 43210 | Email: info@supermart.com
        </p>
      </div>

      <div style="margin-top: 15px;">
        <h3 style="text-align: center; color: #333;">INVOICE</h3>
        <p><strong>Invoice No:</strong> ${invoice.billNumber}</p>
        <p><strong>Date & Time:</strong> ${new Date(invoice.dateTime).toLocaleString()}</p>
        <p><strong>Billed By:</strong> ${invoice.billedBy}</p>
        <p><strong>Payment Method:</strong> ${invoice.paymentMethod}</p>
        ${invoice.paymentDetail ? `<p><strong>Payment Detail:</strong> ${invoice.paymentDetail}</p>` : ''}
        <p><strong>Status:</strong> ${invoice.paymentStatus}</p>
      </div>

      <table style="width: 100%; border-collapse: collapse; margin-top: 15px; font-size: 13px;">
        <thead>
          <tr style="background: #f0f0f0;">
            <th style="border: 1px solid #ccc; padding: 6px;">Item Name</th>
            <th style="border: 1px solid #ccc; padding: 6px;">Qty</th>
            <th style="border: 1px solid #ccc; padding: 6px;">Price/Unit (₹)</th>
            <th style="border: 1px solid #ccc; padding: 6px;">GST (%)</th>
            <th style="border: 1px solid #ccc; padding: 6px;">GST Amt (₹)</th>
            <th style="border: 1px solid #ccc; padding: 6px;">Total (₹)</th>
          </tr>
        </thead>
        <tbody>
          ${invoice.items.map(item => `
            <tr>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.itemName}</td>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.quantity}</td>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.pricePerUnit.toFixed(2)}</td>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.taxPercent}%</td>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.taxAmount.toFixed(2)}</td>
              <td style="border: 1px solid #ccc; padding: 6px;">${item.totalPrice.toFixed(2)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>

      <div style="text-align: right; margin-top: 20px; font-size: 14px;">
        <p><strong>Subtotal:</strong> ₹${invoice.subtotal.toFixed(2)}</p>
        <p><strong>Total GST:</strong> ₹${this.getTotalGST(invoice).toFixed(2)}</p>
        <h3 style="color: #007bff;">Grand Total: ₹${invoice.totalAmount.toFixed(2)}</h3>
      </div>

      <div style="margin-top: 40px; text-align: right;">
        <p>_______________________</p>
        <p><strong>Authorized Signature</strong></p>
      </div>

      <div style="text-align: center; font-size: 12px; margin-top: 20px; border-top: 1px solid #ccc; padding-top: 8px;">
        <p>Thank you for shopping with SuperMart ERP!</p>
        <p>Visit again 😊</p>
      </div>
    </div>
  `;

  const popupWin = window.open('', '_blank', 'width=800,height=900');
  if (popupWin) {
    popupWin.document.open();
    popupWin.document.write(`
      <html>
        <head>
          <title>Invoice #${invoice.billNumber}</title>
        </head>
        <body onload="window.print(); window.close();">${printContents}</body>
      </html>
    `);
    popupWin.document.close();
  }
}

  downloadBillPdf(billId: number): void {
    this.BillingService.downloadBillPdf(billId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Invoice_${billId}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        alert('✅ PDF downloaded successfully!');
      },
      error: (err) => {
        console.error('❌ Error downloading PDF:', err);
        alert('Failed to download PDF.');
      }
    });
  }

  downloadBillExcel(billId: number): void {
    this.BillingService.downloadBillExcel(billId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `Invoice_${billId}.xlsx`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        alert('✅ Excel downloaded successfully!');
      },
      error: (err) => {
        console.error('❌ Error downloading Excel:', err);
        alert('Failed to download Excel.');
      }
    });
  }


  goBackToDashboard() {
    this.router.navigate(['/dashboard']); // replace with your dashboard route
  }
}
