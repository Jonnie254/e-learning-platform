import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CourseDetailsResponse, CourseResponse, PageResponse} from '../interfaces/responses';
import {EnrollmentService} from './enrollment.service';
import {map} from 'rxjs';
import {AuthService} from './auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {
  private baseUrl: string = 'http://localhost:8222/api/v1/courses';

  constructor(
    private httpClient: HttpClient,
    private enrollmentService: EnrollmentService,
    private authService: AuthService
  ) {}

  // method to get all courses from the backend
  getAllCourses(page: { size: number; page: number }, size: number) {
    return this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/all-courses`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      }
    });
  }
  // method to get a course by its id
  getCourseById(courseId: string) {
    return this.httpClient.get<CourseDetailsResponse>(`${this.baseUrl}/${courseId}`);
  }

  // method to get filtered courses
  getFilteredCourses(page: { size: number; page: number }, size: number) {
    const token = this.authService.getToken();
    const coursesObservable = token
      ? this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/available-for-user`, {
        params: {
          page: page.page.toString(),
          size: page.size.toString()
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      : this.getAllCourses(page, size);

    return coursesObservable.pipe(
      map((response: PageResponse<CourseResponse>) => {
        const cartItems = this.enrollmentService.getCartItems();

        // Remove courses that are in the cart
        const filteredCourses = response.content?.filter(course =>
          !cartItems.some(cartItem => cartItem.courseId === course.courseId)
        ) || [];

        return {
          ...response,
          content: filteredCourses,
        };
      })
    );
  }

  //method to get courses that are available for enrollment
  getAvailableCourses(page: {size: number; page: number}, size: number) {
    const token = this.authService.getToken();
   return this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/available-for-user`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
   }
}
