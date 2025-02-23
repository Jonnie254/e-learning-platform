import { Injectable } from '@angular/core';
import {AuthService} from './auth-service.service';
import {HttpClient} from '@angular/common/http';
import {
  CourseEnrollmentResponse,
  EnrollmentStatsResponse,
} from '../interfaces/anayltics';
import {PageResponse} from '../interfaces/responses';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private enrollmentsUrl = 'http://localhost:8222/api/v1/enrollments';
  private courseUrl: string = 'http://localhost:8222/api/v1/courses';

  constructor(private authService: AuthService,
              private http: HttpClient) {
  }

  getInstructorEnrollmentStats() {
    const token = this.authService.getToken();
    return this.http.get<EnrollmentStatsResponse>(`${this.enrollmentsUrl}/total-instructor-enrollments`,{
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAdminEnrollmentStats() {
    const token = this.authService.getToken();
    return this.http.get<EnrollmentStatsResponse>(`${this.enrollmentsUrl}/total-admin-enrollments`,{
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getInstructorCoursesEnrollmentStats(page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CourseEnrollmentResponse>>(`${this.enrollmentsUrl}/instructors-total-course-enrollment`,{
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


