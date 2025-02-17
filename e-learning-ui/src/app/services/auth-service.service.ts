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
  private userRoleSubject = new BehaviorSubject<string | null>(null);
  redirectUrl: string | null = null;

  constructor(private http: HttpClient, private router: Router) {
    this.checkAuthStatus();
  }

  // Method to get the token from localStorage
  getToken(): string {
    return localStorage.getItem('token') || '';
  }

  // Method to get the user role
  get userRole$(): Observable<string | null> {
    return this.userRoleSubject.asObservable();
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
        if (res && res.role) {
          this.userRoleSubject.next(res.role);
          this.redirectBasedOnRole(res.role);
        } else {
        }
      }),
      catchError((err) => {
        this.isAuthenticatedSubject.next(false);
        return of(null);
      })
    );
  }

  // Check if user is authenticated and set the role if needed
  checkAuthStatus() {
    const token = localStorage.getItem('token');
    const isAuthenticated = !!token;
    this.isAuthenticatedSubject.next(isAuthenticated);

    // Only fetch user details if the role is not already set
    if (isAuthenticated && !this.userRoleSubject.value) {
      this.getUserDetails().subscribe({
        next: (res: UserDetailsResponse) => {
          if (res.id) {
            this.userIdSubject.next(res.id);
            this.userRoleSubject.next(res.role);
          } else {
            this.userIdSubject.next('');
          }
        },
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            this.userIdSubject.next('');
            localStorage.removeItem('token');
            this.router.navigate(['/landing-page']);
          }
        }
      });
    }
  }

  get AuthChanged$(): Observable<boolean> {
    return this.isAuthenticatedSubject.asObservable();
  }

  get userId$(): Observable<string> {
    return this.userIdSubject.asObservable();
  }

  // Get user details from the API
  getUserDetails() {
    const token = localStorage.getItem('token');
    return this.http.get<UserDetailsResponse>(`${this.baseUrl}/user-details`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  // Redirect based on the role
  private redirectBasedOnRole(role: string) {
    let redirectUrl = '';
    switch (role) {
      case 'ADMIN':
        redirectUrl = '/dashboard';
        break;
      case 'INSTRUCTOR':
        redirectUrl = '/dashboard';
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

  // Logout the user and redirect to login page
  logout() {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.userRoleSubject.next(null);
    this.userIdSubject.next('');
    this.router.navigate(['/landing-page']);
  }

  // Check if the user is authenticated
  isAuthenticated() {
    return this.isAuthenticatedSubject.value;
  }

  // Make a request to change the user's role
  makeRoleRequest() {
    const token = this.getToken();
    const body = {
      roleRequest: { requestedRole: "INSTRUCTOR" },
      status: "PENDING"
    };

    return this.http.post(`${this.baseUrl}/request-instructor`, body, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}
