import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
 
export interface AttendanceRecord {
  employeeId: string;
  date?: string;
  firstIn?: string;
  lastOut?: string;
  signInTime?: string;
  signOutTime?: string;
  status?: string;
  totalWorkHrs?: string;
  breakHrs?: string;
  actualWorkHrs?: string;
  shortfallHrs?: string;
  excessHrs?: string;
  lateIn?: string;
  earlyOut?: string;
  workHrs?: string;
  shift?: string;
  session1Start?: string;
  session1End?: string;
  session2Start?: string;
  session2End?: string;
  shiftStart?: string;
  shiftEnd?: string;
}
 
export interface Holiday {
  date: string;
  name: string;
}
 
@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
 
  private baseUrl = `${environment.apiUrl}/employee`;
 
  constructor(private http: HttpClient) {}
 
  // ✅ Add JWT token to every request
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // token saved at login
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: token ? `Bearer ${token}` : ''
    });
  }
 
  // ================= Employee APIs =================
  addEmployee(emp: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/addemp`, emp, {
      headers: this.getAuthHeaders()
    });
  }
 
  getAllEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/getallemp`, {
      headers: this.getAuthHeaders()
    });
  }
 
  getActiveEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/active`, {
      headers: this.getAuthHeaders()
    });
  }
 
  // ================= Attendance APIs =================
 
  signIn(record: AttendanceRecord): Observable<AttendanceRecord> {
    return this.http.post<AttendanceRecord>(`${this.baseUrl}/signin`, record, {
      headers: this.getAuthHeaders()
    });
  }
 
  signOut(record: AttendanceRecord): Observable<AttendanceRecord> {
    return this.http.put<AttendanceRecord>(`${this.baseUrl}/signout`, record, {
      headers: this.getAuthHeaders()
    });
  }
 
  getAllAttendance(): Observable<AttendanceRecord[]> {
    return this.http.get<AttendanceRecord[]>(`${this.baseUrl}/allattendance`, {
      headers: this.getAuthHeaders()
    });
  }
 
  deleteAttendance(employeeId: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/delete/${employeeId}`, {
      headers: this.getAuthHeaders()
    });
  }
 
  getMonthlyAttendance(employeeId: string, year: number, month: number): Observable<AttendanceRecord[]> {
    return this.http.get<AttendanceRecord[]>(`${this.baseUrl}/attendance/${employeeId}/${year}/${month}`, {
      headers: this.getAuthHeaders()
    });
  }
  getLoggedEmployee(): Observable<any> {
  const headers = this.getAuthHeaders();
  return this.http.get(`${this.baseUrl}/me`, { headers });
}
 
 
  // ================= Leave APIs =================
  applyLeave(dto: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/applyleave`, dto, {
      headers: this.getAuthHeaders()
    });
  }
// cancelLeaveById(leaveId: number): Observable<any> {
//   return this.http.put(`${this.baseUrl}/cancel/${leaveId}`, {}, {
//     headers: this.getAuthHeaders()
//   });
// }
 
 getLeaveBalance(employeeId: string): Observable<any> {
    // employeeId parameter is ignored by backend; still include it for logging/debug
    return this.http.get(`${this.baseUrl}/balance`, { headers: this.getAuthHeaders() });
  }
cancelLeave(leaveId: string): Observable<any> {
  return this.http.put(`${this.baseUrl}/cancel`, {}, {
    headers: this.getAuthHeaders(),
    responseType: 'text'  // 👈 Important: backend returns plain text, not JSON
  });
}
 
 
 
  getAllLeaves(employeeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/allleave`, {
      headers: this.getAuthHeaders()
    });
  }
 
  grantCompOff(employeeId: string, days: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/compoff`, { employeeId, days }, {
      headers: this.getAuthHeaders()
    });
  }
 
  getPendingLeaves(employeeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/pending`, {
      headers: this.getAuthHeaders()
    });
  }
 
  getLeaveHistory(employeeId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/history`, {
      headers: this.getAuthHeaders()
    });
  }
 
  // ================= Salary APIs =================
 generateSalary(dto: any): Observable<any> {
  return this.http.post(`${this.baseUrl}/salaryslip`, dto, {
    headers: this.getAuthHeaders()
  });
}
 
downloadPayslip(month: string): Observable<Blob> {
  return this.http.get(`${this.baseUrl}/pdfslip/${month}`, {
    headers: this.getAuthHeaders(),
    responseType: 'blob'
  });
}
 
 
// downloadPayslip(month: string): Observable<Blob> {
//   return this.http.get(`${this.baseUrl}/pdfslip/${month}`, {
//     headers: this.getAuthHeaders(),
//     responseType: 'blob'
//   });
// }
 
 
  // ================= Holiday APIs =================
  getAllHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(`${this.baseUrl}/holidays`, {
      headers: this.getAuthHeaders()
    });
  }
}
 
  // downloadPayslip(employeeId: string, month: string): Observable<Blob> {
  //   return this.http.get(`${this.baseUrl}/pdfslip/${employeeId}/${month}`, { responseType: 'blob' });
  // }
 
 
 
 