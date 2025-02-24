import { Component } from '@angular/core';
import {AnalyticsService} from '../../../services/analytics.service';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from '../../../services/auth-service.service';
import {CourseEnrollmentResponse} from '../../../interfaces/anayltics';
import {PageResponse} from '../../../interfaces/responses';
import {NgForOf} from '@angular/common';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';

@Component({
  selector: 'app-enrollment-summary',
  standalone: true,
  imports: [
    NgForOf,
    PaginationComponent
  ],
  templateUrl: './enrollment-summary.component.html',
  styleUrl: './enrollment-summary.component.scss'
})
export class EnrollmentSummaryComponent {
  userRole$: Observable<string | null>;
  courseEnrollmentResponse: PageResponse<CourseEnrollmentResponse> = {} as PageResponse<CourseEnrollmentResponse>;
  userRole: string | null = '';
  page: number = 0;
  size: number = 4;

  constructor(
    private analyticsService: AnalyticsService,
    private authService: AuthService,
    private router: Router) {
    this.userRole$ = this.authService.userRole$;
    this.getUserRole();
  }

  getUserRole() {
    this.userRole$ = this.authService.userRole$;
    this.userRole$.subscribe({
      next: (role) => {
        this.userRole = role;
      }
    });
    this.getDataBasedOnRole();
  }

  getDataBasedOnRole() {
    if (this.userRole === 'ADMIN') {
      this.getAdminTotalCoursesEnrollment();
    } else if (this.userRole === 'INSTRUCTOR') {
      this.getInstructorTotalCoursesEnrollment();
    }
  }

  getInstructorTotalCoursesEnrollment() {
    this.analyticsService.getInstructorCoursesEnrollmentStats({
      size: this.size,
      page: this.page
    }).subscribe({
        next: (res) =>{
          this.courseEnrollmentResponse = res;
        }
      })
  }


  private getAdminTotalCoursesEnrollment() {
    this.analyticsService.getAdminCoursesEnrollmentStats({
      size: this.size,
      page: this.page
    }).subscribe({
        next: (res) =>{
          console.log(res);
          this.courseEnrollmentResponse = res;
        }
      })
  }

  totalPages() {
    return this.courseEnrollmentResponse.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
   this.getDataBasedOnRole();
  }
}

