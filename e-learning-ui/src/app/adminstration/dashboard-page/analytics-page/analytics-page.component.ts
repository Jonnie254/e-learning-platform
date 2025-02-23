import { Component } from '@angular/core';
import {AuthService} from '../../../services/auth-service.service';
import {Observable} from 'rxjs';
import {CommonModule} from '@angular/common';
import {ChartModule} from 'primeng/chart';
import {EnrollmentStatsResponse} from '../../../interfaces/anayltics';
import {AnalyticsService} from '../../../services/analytics.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-analytics-page',
  standalone: true,
  imports: [CommonModule, ChartModule],
  templateUrl: './analytics-page.component.html',
  styleUrl: './analytics-page.component.scss'
})
export class AnalyticsPageComponent {
  userRole$: Observable<string | null>;
  userRole: string | null = '';
  data: any = 10;
  options: any;
  enrollmentStatsResponse: EnrollmentStatsResponse = {} as EnrollmentStatsResponse;

  constructor(
    private authService : AuthService,
    private analyticsService: AnalyticsService,
    private router: Router
    ) {
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
      this.getAdminStats();
    }else if(this.userRole === 'INSTRUCTOR') {
      this.getInstructorStats();
    }
  }

  getInstructorStats() {
    this.analyticsService.getInstructorEnrollmentStats().subscribe({
      next:(res) =>{
        this.enrollmentStatsResponse = res;
      }
    });
  }

  getAdminStats() {
    this.analyticsService.getAdminEnrollmentStats().subscribe({
      next:(res) =>{
        this.enrollmentStatsResponse = res;
      }
    });
  }

  navigateToEnrollmentSummary() {
    this.router.navigate(['/dashboard/analytics/enrollment-summary']);
  }
}
