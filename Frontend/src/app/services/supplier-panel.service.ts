import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

// ================= INTERFACES =================

// Supplier Item
export interface SupplierItem {
  id?: number;
  productId?: string;
  productName: string;
  productType: 'VEGETABLE' | 'FRUIT' | 'GROCERY';
  unitType: 'GM' | 'KG' | 'LITER' | 'QUANTITY';
  quantity: number;
  pricePerUnit: number;
}

// Product details inside purchase order
export interface ProductDetail {
  productId: string;
  productName: string;
  productType: string;
  unitType: string;
  quantity: number | null;
  pricePerUnit: number | null;
}

// Purchase order response
export interface PurchaseOrderResponse {
  orderId: string;
  supplierId: string;
  productIds?: string[];
  products?: ProductDetail[];
  totalAmount: number;
  paymentMode: string;
  paymentStatus: string;
  orderStatus: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'SUCCESS';
  orderDate: Date;
}

@Injectable({
  providedIn: 'root'
})
export class SupplierPanelService {
  private baseUrl = environment.apiUrl;
  private itemUrl = `${this.baseUrl}/supplier/items`;
  private orderUrl = `${this.baseUrl}/supplier/orders`;

  constructor(private http: HttpClient) {}

  // ================== Supplier Items ==================
  getItems(): Observable<SupplierItem[]> {
    return this.http.get<SupplierItem[]>(this.itemUrl, this.httpOptions())
      .pipe(catchError(this.handleError));
  }

  addItem(item: SupplierItem): Observable<SupplierItem> {
    return this.http.post<SupplierItem>(this.itemUrl, item, this.httpOptions())
      .pipe(catchError(this.handleError));
  }

  updateItem(id: number, item: SupplierItem): Observable<SupplierItem> {
    return this.http.put<SupplierItem>(`${this.itemUrl}/${id}`, item, this.httpOptions())
      .pipe(catchError(this.handleError));
  }

  deleteItem(id: number): Observable<void> {
    return this.http.delete<void>(`${this.itemUrl}/${id}`, this.httpOptions())
      .pipe(catchError(this.handleError));
  }

  // ================== Purchase Orders ==================
  getOrders(): Observable<PurchaseOrderResponse[]> {
  const token = localStorage.getItem('token') || '';
  const headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  });

  return this.http.get<PurchaseOrderResponse[]>(this.orderUrl, { headers })
    .pipe(
      map((orders) =>
        orders.map(o => ({
          ...o,
          orderDate: o.orderDate ? new Date(o.orderDate) : new Date(),
          // products array already backend se aa raha hai
        }))
      ),
      catchError(this.handleError)
    );
}


  /**
   * Respond to a purchase order (accept or reject)
   */
  respondToOrder(orderId: string, action: 'accept' | 'reject'): Observable<PurchaseOrderResponse> {
    const token = localStorage.getItem('token') || '';
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });

    return this.http.post<PurchaseOrderResponse>(`${this.orderUrl}/${orderId}/${action}`, {}, { headers })
      .pipe(catchError(this.handleError));
  }

  // ================== HTTP Options ==================
  private httpOptions() {
    const token = localStorage.getItem('token') || '';
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      })
    };
  }

  // ================== Error Handler ==================
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Client error: ${error.error.message}`;
    } else {
      errorMessage = `Server error ${error.status}: ${error.error?.message || error.message}`;
    }
    console.error('SupplierPanelService Error:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
