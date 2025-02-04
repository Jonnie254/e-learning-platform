import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, loginUser, registerUser, Role, User, UserDetailsResponse } from '../interfaces/users';
import {BehaviorSubject, Observable, of} from 'rxjs';
import { Router } from '@angular/router';
import { catchError, switchMap, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  baseUrl: string = 'http://localhost:8222/api/v1/users';
  isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  private userIdSubject = new BehaviorSubject<string>('');
  redirectUrl: string | null = null;

  constructor(private http: HttpClient, private router: Router) {
    this.checkAuthStatus();
  }

  // Register the user
  registerUser(user: registerUser) {
    return this.http.post(`${this.baseUrl}/register`, user);
  }

  // Method to activate the account
  activateAccount(token: string) {
    return this.http.get(`${this.baseUrl}/activate-account`, { params: { token } });
  }

  // Method to login the user
  loginUser(user: loginUser) {
    return this.http.post<AuthResponse>(`${this.baseUrl}/authenticate`, user).pipe(
      tap((res) => {
        if (res.token) {
          localStorage.setItem('token', res.token);
          this.isAuthenticatedSubject.next(true);
        }
      }),
      switchMap(() => this.getUserDetails()),
      tap((res: UserDetailsResponse) => {
        console.log('User details response:', res);
        if (res && res.role) {
          console.log('User role:', res.role);
          this.redirectBasedOnRole(res.role);
        } else {
          console.error('User details are missing!');
        }
      }),
      catchError((err) => {
        console.error('Login failed:', err);
        this.isAuthenticatedSubject.next(false);
        return of(null);
      })
    );
  }

  checkAuthStatus() {
    const token = localStorage.getItem('token');
    const isAuthenticated = !!token;
    this.isAuthenticatedSubject.next(isAuthenticated);
    if (isAuthenticated) {
      this.getUserDetails().subscribe({
        next: (res: UserDetailsResponse) => {
          if (res.id) {
            this.userIdSubject.next(res.id);
          } else {
            this.userIdSubject.next('');
          }
        },
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            this.userIdSubject.next('');
            localStorage.removeItem('token');
            this.router.navigate(['/login']);
          }
        }
      });
    }
  }

  get AuthChanged$(): Observable<boolean>{
    return this.isAuthenticatedSubject.asObservable();
  }

  get userId$(): Observable<string> {
    return this.userIdSubject.asObservable();
  }

  getUserDetails() {
    const token = localStorage.getItem('token');
    return this.http.get<UserDetailsResponse>(`${this.baseUrl}/user-details`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  private redirectBasedOnRole(role: string) {
    let redirectUrl = '';
    switch (role) {
      case 'ADMIN':
        redirectUrl = '/admin-dashboard';
        break;
      case 'INSTRUCTOR':
        redirectUrl = '/instructor-dashboard';
        break;
      case 'STUDENT':
        redirectUrl = '/landing-page';
        break;
      default:
        console.error('Unknown role:', role);
    }
    const redirect = this.redirectUrl ? this.redirectUrl : redirectUrl;
    this.router.navigate([redirect]);
  }

  logout() {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.userIdSubject.next('');
    this.router.navigate(['/login']);
  }

  isAuthenticated() {
    return this.isAuthenticatedSubject.value;
  }
}
