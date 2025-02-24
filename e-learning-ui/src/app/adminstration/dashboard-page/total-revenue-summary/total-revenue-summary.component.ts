import { Component } from '@angular/core';
import {Observable} from 'rxjs';
import {AuthService} from '../../../services/auth-service.service';
import {AnalyticsService} from '../../../services/analytics.service';
import {CurrencyPipe, NgForOf} from '@angular/common';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';
import {PageResponse} from '../../../interfaces/responses';
import {CourseEarningResponse} from '../../../interfaces/anayltics';

@Component({
  selector: 'app-total-revenue-summary',
  standalone: true,
  imports: [
    NgForOf,
    PaginationComponent,
    CurrencyPipe
  ],
  templateUrl: './total-revenue-summary.component.html',
  styleUrl: './total-revenue-summary.component.scss'
})
export class TotalRevenueSummaryComponent {
  userRole$: Observable<string | null>;
  userRole: string | null = '';
  page: number = 0;
  size: number = 11;
  courseEarningSummary: PageResponse<CourseEarningResponse> = {} as PageResponse<CourseEarningResponse>;

  constructor(
    private authService : AuthService,
    private analyticsService: AnalyticsService,

  ) {
    this.userRole$ = this.authService.userRole$;
    this.userRole$.subscribe({
      next: (role) => {
        this.userRole = role;
        this.getDataBasedOnRole();
      }
    });
  }

  private getDataBasedOnRole() {
    if (!this.userRole) return;
    if (this.userRole === 'ADMIN') {
      this.getAdminTotalCourseRevenue();
    } else if (this.userRole === 'INSTRUCTOR') {
        this.getInstructorTotalCourseRevenue();
    }
  }

  getInstructorTotalCourseRevenue() {
    this.analyticsService.getInstructorTotalCourseRevenue({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) =>{
        console.log(res);
        this.courseEarningSummary = res;
      }
    });
  }

  getAdminTotalCourseRevenue() {
    this.analyticsService.getAdminTotalCourseRevenue({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) =>{
        console.log(res);
        this.courseEarningSummary = res;
      }
    });
  }

  totalPages() {
    return this.courseEarningSummary.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.getDataBasedOnRole();
  }
}
