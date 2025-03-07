import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth-service.service';
import {Cart, enrollmentResponse, SectionStatus} from '../interfaces/responses';
import {BehaviorSubject, of} from 'rxjs';
import {catchError, switchMap, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private baseUrl = 'http://localhost:8222/api/v1/enrollments';
  private cartSubject: BehaviorSubject<Cart> = new BehaviorSubject<Cart>
  ({cartId: '', totalAmount: 0, reference: '', cartItems: []});
  cart$ = this.cartSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    this.getCart()?.subscribe();
  }

  // method to get the user's cart
  getCart() {
    const token = this.authService.getToken();
    if (!token) {
      console.error('No token found');
      return;
    }
    return this.http.get<Cart>(`${this.baseUrl}/get-cart`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      tap((cart: Cart) => {
        if (!cart.cartId) {
       console.log('No cart found');
        } else {
          console.log('Cart fetched successfully:', cart);
        }
        this.cartSubject.next(cart);
      }),
      catchError((error) => {
        return of({ cartId: '', totalAmount: 0, reference: '', status: 'ACTIVE', cartItems: [] }); // Return empty cart
      })
    );
  }


  addCartItem(courseId: string) {
    const token = this.authService.getToken();
    return this.http.post(`${this.baseUrl}/add-course-cart/${courseId}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      switchMap(() => this.http.get<Cart>(`${this.baseUrl}/get-cart`, {
        headers: { Authorization: `Bearer ${token}` }
      })),
      tap((cart) => this.cartSubject.next(cart))
    );
  }

  //method to remove a course from the cart
  removeCartItem(courseId: string) {
    const token = this.authService.getToken();
    return this.http.delete(`${this.baseUrl}/remove-cart-item/${courseId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      switchMap(() => this.http.get<Cart>(`${this.baseUrl}/get-cart`, {
        headers: { Authorization: `Bearer ${token}` }
      })),
      tap((cart) => this.cartSubject.next(cart))
    );
  }

  checkout() {
    const token = this.authService.getToken();
    return this.http.post<{ approvalUrl: string }>(`${this.baseUrl}/checkout-cart`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  //method to get the cart items
  getCartItems() {
    const cart = this.cartSubject.getValue();
    return cart.cartItems || [];
  }

  //method to get users enrollment
  getEnrollments() {
    const token = this.authService.getToken();
    return this.http.get<enrollmentResponse>(`${this.baseUrl}/enrolled-courses-details`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  //method to get the section status
  getSectionStatus(sectionId: string) {
    const token = this.authService.getToken();
    return this.http.get<SectionStatus>(`${this.baseUrl}/get-section-status/${sectionId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });

  }

  //method to complete a section
  completeSection(sectionId: string) {
    const token = this.authService.getToken();
    return this.http.put(`${this.baseUrl}/toggle-section-status/${sectionId}`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  //method to get the progress of the course
  getCourseProgess(courseId:string){
    const token = this.authService.getToken();
    return this.http.get<{progress: number}>(`${this.baseUrl}/get-progress-by-course/${courseId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

}

