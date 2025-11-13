import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  // ✅ Base URLs for backend APIs
  private BASE_URL = environment.apiUrl; // change port if needed
  private AUTH_URL = `${this.BASE_URL}/api/auth`;
  private ROLE_URL = `${this.BASE_URL}/api/roles`;
  private EMAIL_URL = `${this.BASE_URL}/email`;

  constructor(private http: HttpClient) { }

  // ✅ LOGIN
  login(username: string, password: string, role: string): Observable<any> {
    const body = { username, password, role };
    return this.http.post(`${this.AUTH_URL}/login`, body);
  }

  // ✅ REGISTER
  register(username: string, password: string, role: string): Observable<any> {
    const body = { username, password, role };
    return this.http.post(`${this.AUTH_URL}/register`, body);
  }

  // ✅ GET ALL ROLES
  getAllRoles(): Observable<any> {
    return this.http.get(`${this.ROLE_URL}`);
  }

  // ✅ SEND EMAIL
  sendEmail(to: string, subject: string, body: string): Observable<any> {
    const params = new HttpParams()
      .set('to', to)
      .set('subject', subject)
      .set('body', body);
    return this.http.post(`${this.EMAIL_URL}/send`, null, { params });
  }
}
