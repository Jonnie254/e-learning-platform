import { Component } from '@angular/core';
import {AuthService} from '../../../services/auth-service.service';
import {Observable} from 'rxjs';
import {CommonModule} from '@angular/common';
import {ChartModule} from 'primeng/chart';
import {EnrollmentStatsResponse, TotalCoursesResponse, TotalRevenueStatsResponse} from '../../../interfaces/anayltics';
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
  revenueChartData: any;
  enrollmentChartData: any;
  coursesChartData: any;
  chartOptions: any;
  enrollmentStatsResponse: EnrollmentStatsResponse = {} as EnrollmentStatsResponse;
  totalRevenueStatsResponse: TotalRevenueStatsResponse = {} as TotalRevenueStatsResponse;
  totalCoursesResponse: TotalCoursesResponse = {} as TotalCoursesResponse;

  constructor(
    private authService : AuthService,
    private analyticsService: AnalyticsService,
    private router: Router
    ) {
    this.userRole$ = this.authService.userRole$;
    this.userRole$.subscribe({
      next: (role) => {
        this.userRole = role;
        this.getDataBasedOnRole();
      }
    });
    this.setupChartOptions();
  }

  getDataBasedOnRole() {
    if (!this.userRole) return;
    if (this.userRole === 'ADMIN') {
      this.getAdminStats();
      this.getAdminRevenueStats();
      this.getAdminTotalCourses();
    } else if (this.userRole === 'INSTRUCTOR') {
      this.getInstructorStats();
      this.getInstructionalRevenueStats();
      this.getInstructorTotalCourses();
    }
  }


  getInstructorStats() {
    this.analyticsService.getInstructorEnrollmentStats().subscribe({
      next:(res) =>{
        this.enrollmentStatsResponse = res;
        this.updateEnrollmentChart();
      }
    });
  }

  getAdminStats() {
    this.analyticsService.getAdminEnrollmentStats().subscribe({
      next:(res) =>{
        this.enrollmentStatsResponse = res;
        this.updateEnrollmentChart();
      }
    });
  }

  getInstructionalRevenueStats() {
    this.analyticsService.getInstructorTotalRevenueStats().subscribe({
      next:(res) =>{
        this.totalRevenueStatsResponse = res;
        this.updateRevenueChart();
      }
    });
  }

  getAdminRevenueStats() {
    this.analyticsService.getAdminTotalRevenueStats().subscribe({
      next:(res) =>{
        this.totalRevenueStatsResponse = res;
        this.updateRevenueChart();
      }
    });
  }

  getInstructorTotalCourses(){
   this.analyticsService.getInstructorTotalCourses().subscribe({
      next:(res) =>{
        this.totalCoursesResponse = res;
        this.updateCoursesChart();
      }
   });
  }

  getAdminTotalCourses(){
    this.analyticsService.getAdminTotalCourses().subscribe({
        next:(res) =>{
          this.totalCoursesResponse = res;
          this.updateCoursesChart();
        }
    });
  }

  setupChartOptions() {
    this.chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: true, position: 'top' },
        tooltip: { enabled: true }
      },
      scales: {
        x: { display: true },
        y: { display: true }
      }
    };
  }

  updateRevenueChart() {
    this.revenueChartData = {
      labels: ['Total Revenue'],
      datasets: [
        {
          label: 'Revenue in USD',
          data: [this.totalRevenueStatsResponse.totalEarning ?? 0],
          backgroundColor: ['#4CAF50'],
          borderColor: ['#388E3C'],
          borderWidth: 1
        }
      ]
    };
  }

  updateEnrollmentChart() {
    this.enrollmentChartData = {
      labels: ['Total Enrollments'],
      datasets: [
        {
          label: 'Enrollments',
          data: [this.enrollmentStatsResponse.totalEnrollments ?? 0],
          backgroundColor: ['#2196F3'],
          borderColor: ['#1976D2'],
          borderWidth: 1
        }
      ]
    };
  }

  updateCoursesChart() {
    this.coursesChartData = {
      labels: ['Total Courses'],
      datasets: [
        {
          label: 'Courses',
          data: [this.totalCoursesResponse.totalCourses ?? 0],
          backgroundColor: ['#FF9800'],
          borderColor: ['#F57C00'],
          borderWidth: 1
        }
      ]
    };
  }

  navigateToEnrollmentSummary() {
    this.router.navigate(['/dashboard/analytics/enrollment-summary']);
  }

  navigateToRevenueSummary() {
    this.router.navigate(['/dashboard/analytics/revenue-summary']);

  }
}
