import { HttpClient, HttpHeaders } from '@angular/common/http';
 
import { Injectable } from '@angular/core';
 
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
 
// 🧍 Employee Interface
 
export interface Employee {
 
  employeeId?: string;
 
  name: string;
 
  email: string;
 
  role: string;
 
  phone?: string;
 
}
 
// 🏭 Supplier Interface (matches your backend Supplier.java)
 
// ✅ Supplier Model
export interface Supplier {
  supplierId: string;
  name: string;
  email: string;
  phone: string;
  address: string;
  type: string;
}
 
export interface Product {
  productId: string;
  productName: string;
  category: string;
  unit: string;
  quantity: number;
  pricePerUnit: number;
  supplierId: string;
}
 
// ✅ OrderItem
export interface OrderItem {
  itemname: string;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
}
 
// ✅ PurchaseOrder
// export interface PurchaseOrder {
//   orderId?: number;
//   supplierId: number;
//   items: OrderItem[];
//   orderDate: string;
//   totalAmount: number;
//   status: string;
// }
 
// 🧾 Purchase Order Model (matches your backend PurchaseOrder.java)
 
export interface PurchaseOrder {
  orderId: string;
  supplierId: string;
  totalAmount: number;
  paymentMode: string;
  paymentStatus: string;
  orderStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED';
  orderDate?: string;
}
 
// 🏪 Inventory Model
 
export interface Inventory {
 
  itemName: string;
 
  quantity: number;
 
  price: number;
 
  unit: string;
 
  category?: string;
 
}
 
@Injectable({
 
  providedIn: 'root'
 
})
 
export class AdminService {
 
  private baseUrl = `${environment.apiUrl}/admin`; // ✅ Make sure this matches your backend port
 
  constructor(private http: HttpClient) {}
 
  // 🔐 Get token for secure APIs
 
  private getAuthHeaders(): HttpHeaders {
 
    const token = localStorage.getItem('token');
 
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
 
    if (token) headers = headers.set('Authorization', `Bearer ${token}`);
 
    return headers;
 
  }
 
  // 🧍 EMPLOYEE APIs
 
  addEmployee(emp: Employee): Observable<Employee> {
 
    return this.http.post<Employee>(`${this.baseUrl}/employees`, emp, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  getAllEmployees(): Observable<Employee[]> {
 
    return this.http.get<Employee[]>(`${this.baseUrl}/employees`, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  getEmployeeById(employeeId: string): Observable<Employee> {
 
    return this.http.get<Employee>(`${this.baseUrl}/employees/${employeeId}`, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  deleteEmployee(employeeId: string): Observable<string> {
 
    return this.http.delete(`${this.baseUrl}/employees/${employeeId}`, {
 
      headers: this.getAuthHeaders(),
 
      responseType: 'text',
 
    });
 
  }
 
  // 🏭 SUPPLIER APIs
 
  addSupplier(supplier: Supplier): Observable<Supplier> {
 
    return this.http.post<Supplier>(`${this.baseUrl}/suppliers`, supplier, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  getAllSuppliers(): Observable<Supplier[]> {
 
    return this.http.get<Supplier[]>(`${this.baseUrl}/suppliers`, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  // 🏪 INVENTORY APIs
 
  addInventory(stock: Inventory): Observable<Inventory> {
 
    return this.http.post<Inventory>(`${this.baseUrl}/inventory`, stock, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  getAllInventory(): Observable<Inventory[]> {
 
    return this.http.get<Inventory[]>(`${this.baseUrl}/inventory`, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  getInventoryByProductId(productId: string): Observable<Inventory> {
 
    return this.http.get<Inventory>(`${this.baseUrl}/inventory/${productId}`, {
 
      headers: this.getAuthHeaders(),
 
    });
 
  }
 
  // // 🧾 PURCHASE ORDER APIs
 
  // addPurchaseOrder(order: PurchaseOrder): Observable<PurchaseOrder> {
 
  //   return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders`, order, {
 
  //     headers: this.getAuthHeaders(),
 
  //   });
 
  // }
 
  // getOrders(): Observable<PurchaseOrder[]> {
 
  //   return this.http.get<PurchaseOrder[]>(`${this.baseUrl}/purchase-orders`, {
 
  //     headers: this.getAuthHeaders(),
 
  //   });
 
  // }
 
  // deleteOrder(id: number): Observable<void> {
 
  //   return this.http.delete<void>(`${this.baseUrl}/purchase-orders/${id}`, {
 
  //     headers: this.getAuthHeaders(),
 
  //   });
 
  // }
 
  // updateOrderStatus(orderId: number, status: string): Observable<PurchaseOrder> {
 
  //   return this.http.post<PurchaseOrder>(
 
  //     `${this.baseUrl}/purchase-orders/${orderId}/status?status=${status}`,
 
  //     {},
 
  //     { headers: this.getAuthHeaders() }
 
  //   );
 
  // }
 
  // // 🛒 PRODUCTS API (optional — only if you add /products endpoint in backend)
 
  // getProducts(): Observable<Product[]> {
 
  //   return this.http.get<Product[]>(`${this.baseUrl}/products`, {
 
  //     headers: this.getAuthHeaders(),
 
  //   });
 
  // }
 
  // 🧾 PURCHASE ORDER APIs
  addPurchaseOrder(order: PurchaseOrder): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(`${this.baseUrl}/purchase-orders`, order, {
      headers: this.getAuthHeaders(),
    });
  }
 
  getOrders(): Observable<PurchaseOrder[]> {
    return this.http.get<PurchaseOrder[]>(`${this.baseUrl}/purchase-orders`, {
      headers: this.getAuthHeaders(),
    });
  }
 
  deleteOrder(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/purchase-orders/${id}`, {
      headers: this.getAuthHeaders(),
    });
  }
 
  updateOrderStatus(orderId: string, status: string): Observable<PurchaseOrder> {
    return this.http.post<PurchaseOrder>(
      `${this.baseUrl}/purchase-orders/${orderId}/status?status=${status}`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }
 
  // 🛒 PRODUCTS API (optional — only if you add /products endpoint in backend)
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.baseUrl}/inventory`, {
      headers: this.getAuthHeaders(),
    });
  }
 
}
 