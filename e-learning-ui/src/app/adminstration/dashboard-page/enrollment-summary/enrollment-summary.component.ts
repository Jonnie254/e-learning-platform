import { Component } from '@angular/core';
import {AnalyticsService} from '../../../services/analytics.service';
import {Router} from '@angular/router';
import {Observable} from 'rxjs';
import {AuthService} from '../../../services/auth-service.service';
import {CourseEnrollmentResponse} from '../../../interfaces/anayltics';
import {PageResponse} from '../../../interfaces/responses';

@Component({
  selector: 'app-enrollment-summary',
  standalone: true,
  imports: [],
  templateUrl: './enrollment-summary.component.html',
  styleUrl: './enrollment-summary.component.scss'
})
export class EnrollmentSummaryComponent {
  userRole$: Observable<string | null>;
  CourseEnrollmentResponse: PageResponse<CourseEnrollmentResponse> = {} as PageResponse<CourseEnrollmentResponse>;
  userRole: string | null = '';
  page: number = 0;
  size: number = 8;

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
      //this.getTotalEnrollment();
    } else if (this.userRole === 'INSTRUCTOR') {
      this.getInstructorTotalCoursesEnrollment({ size: this.size, page: this.page });

    }
  }

  getInstructorTotalCoursesEnrollment(param: { size: number; page: number }) {
    this.analyticsService.getInstructorCoursesEnrollmentStats({ size: param.size, page: param.page })
      .subscribe({
        next: (res) =>{
          console.log(res);
          this.CourseEnrollmentResponse = res;
        }
      })
  }
}

