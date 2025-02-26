import { Injectable } from '@angular/core';
import {AuthService} from './auth-service.service';
import {HttpClient} from '@angular/common/http';
import {
  CourseEarningResponse,
  CourseEnrollmentResponse,
  EnrollmentStatsResponse, TotalCoursesResponse, TotalRevenueStatsResponse,
} from '../interfaces/anayltics';
import {PageResponse} from '../interfaces/responses';
import {RoleResponse, UserDetailsResponse} from '../interfaces/users';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private usersUrl: string = 'http://localhost:8222/api/v1/users';
  private enrollmentsUrl = 'http://localhost:8222/api/v1/enrollments';
  private courseUrl: string = 'http://localhost:8222/api/v1/courses';
  private paymentUrl: string = 'http://localhost:8222/api/v1/payments';

  constructor(private authService: AuthService,
              private http: HttpClient) {
  }

  getInstructorEnrollmentStats() {
    const token = this.authService.getToken();
    return this.http.get<EnrollmentStatsResponse>(`${this.enrollmentsUrl}/total-instructor-enrollments`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAdminEnrollmentStats() {
    const token = this.authService.getToken();
    return this.http.get<EnrollmentStatsResponse>(`${this.enrollmentsUrl}/total-admin-enrollments`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getInstructorCoursesEnrollmentStats(page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CourseEnrollmentResponse>>(`${this.enrollmentsUrl}/instructors-total-course-enrollment`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAdminCoursesEnrollmentStats(page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CourseEnrollmentResponse>>(`${this.enrollmentsUrl}/admin-total-course-enrollment`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getInstructorTotalRevenueStats() {
    const token = this.authService.getToken();
    return this.http.get<TotalRevenueStatsResponse>(`${this.paymentUrl}/instructors-total-revenue`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAdminTotalRevenueStats() {
    const token = this.authService.getToken();
    return this.http.get<TotalRevenueStatsResponse>(`${this.paymentUrl}/admin-total-revenue`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getInstructorTotalCourses(){
    const token = this.authService.getToken();
    return this.http.get<TotalCoursesResponse>(`${this.courseUrl}/instructor-total-courses`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getAdminTotalCourses(){
    const token = this.authService.getToken();
    return this.http.get<TotalCoursesResponse>(`${this.courseUrl}/admin-total-courses`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getInstructorTotalCourseRevenue(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CourseEarningResponse>>(`${this.paymentUrl}/instructor-revenue-summary`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  getAdminTotalCourseRevenue(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CourseEarningResponse>>(`${this.paymentUrl}/admin-revenue-summary`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  getStudentUsers(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<UserDetailsResponse>>(`${this.usersUrl}/all-active-students`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  getInstructorUsers(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<UserDetailsResponse>>(`${this.usersUrl}/all-active-instructors`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  getRoleRequests(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<RoleResponse>>(`${this.usersUrl}/all-role-requests-pending`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  getRejectedRoleRequests(param: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<RoleResponse>>(`${this.usersUrl}/all-role-requests-rejected`, {
      params: {
        page: param.page.toString(),
        size: param.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  processRoleRequest(requestId: string, userId: string, action: string) {
    const token = this.authService.getToken();
    return this.http.put(`${this.usersUrl}/process-role-request`, {} ,{
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
      'request-id': requestId,
      'user-id': userId,
      'action': action
      }
    });
  }
}
