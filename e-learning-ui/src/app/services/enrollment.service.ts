import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth-service.service';
import {UserDetailsResponse} from '../interfaces/users';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private baseUrl = 'http://localhost:8222/api/v1/enrollments';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  //method to add the cart item to the cart
  addCartItem(courseId: string) {
    console.log('Course ID:', courseId);
    console.log('Token:', this.authService.getToken());
    const token = this.authService.getToken();

    return this.http.post(`${this.baseUrl}/add-course-cart/${courseId}`, {}, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}

