import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BillingPanelService {

  // ✅ Single base URL (update if backend port or host changes)
  private readonly BASE_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /** 🏪 Get all inventory products (from AdminController) */
  getAllProducts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE_URL}/admin/inventory`);
  }

  /** 🧾 Create a new bill */
  createBill(billData: any): Observable<any> {
    return this.http.post(`${this.BASE_URL}/api/billing/create`, billData);
  }

  getAllBills() {
  return this.http.get<any[]>(`${this.BASE_URL}/api/billing/all`);
}

  /** 📄 Download bill as PDF */
  downloadBillPdf(billId: number): Observable<Blob> {
    return this.http.get(`${this.BASE_URL}/api/billing/download/pdf/${billId}`, {
      responseType: 'blob'
    });
  }

  /** 📊 Download bill as Excel */
  downloadBillExcel(billId: number): Observable<Blob> {
    return this.http.get(`${this.BASE_URL}/api/billing/download/excel/${billId}`, {
      responseType: 'blob'
    });
  }

  /** 📚 (Optional) Fetch all invoices */
  getAllInvoices(): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE_URL}/api/billing/invoices`);
  }
}
