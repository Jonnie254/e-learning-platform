import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth-service.service';
import {Cart, EnrollmentResponse, FeedbackRequest, RatingResponse, SectionStatus} from '../interfaces/responses';
import {BehaviorSubject, of, take} from 'rxjs';
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
      return of(null);
    }
    return this.authService.userRole$.pipe(
      take(1),
      switchMap(role => {
        if (role !== 'STUDENT') {
          return of(null);
        }
        return this.http.get<Cart>(`${this.baseUrl}/get-cart`, {
          headers: { Authorization: `Bearer ${token}` }
        }).pipe(
          tap((cart: Cart) => {
            this.cartSubject.next(cart);
          }),
          catchError(() => {
            const emptyCart = { cartId: '', totalAmount: 0, reference: '', status: 'ACTIVE', cartItems: [] };
            this.cartSubject.next(emptyCart);
            return of(emptyCart);
          })
        );
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
    return this.http.get<EnrollmentResponse>(`${this.baseUrl}/enrolled-courses-details`, {
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

  //method to get the progress and check whether course is rated
  getRatingStatus(courseId: string) {
    const token = this.authService.getToken();
    return this.http.get<RatingResponse>(`${this.baseUrl}/check-course-rating/${courseId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  //method to submit the rating
  submitRating(courseId: String, feedbackRequest:FeedbackRequest){
    const token = this.authService.getToken();
    return this.http.post(`${this.baseUrl}/create-feedback/${courseId}`, feedbackRequest, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

}

