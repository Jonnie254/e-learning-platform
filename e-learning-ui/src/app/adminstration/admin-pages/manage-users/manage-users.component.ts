import { Component } from '@angular/core';
import {NgClass, NgForOf} from '@angular/common';
import {UserDetailsResponse} from '../../../interfaces/users';
import {Observable} from 'rxjs';
import {AuthService} from '../../../services/auth-service.service';
import {AnalyticsService} from '../../../services/analytics.service';
import {PageResponse} from '../../../interfaces/responses';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [
    NgForOf,
    NgClass,
    PaginationComponent
  ],
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.scss'
})
export class ManageUsersComponent {
  userDetails: PageResponse<UserDetailsResponse> = {} as PageResponse<UserDetailsResponse>;
  userRole$: Observable<string | null>;
  userRole: string | null = '';
  page: number = 0;
  size: number = 4;
  selectedFilter: string = 'INSTRUCTOR';

  constructor(
    private authService: AuthService,
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
     this.applyFilter();
    }

  }

  getStudentUsers() {
    this.analyticsService.getStudentUsers({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) => {
       this.userDetails = res;
      }
    });
  }

  getInstructorUsers() {
    this.analyticsService.getInstructorUsers({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) => {
        this.userDetails = res;
      }
    });
  }
  applyFilter() {
    if (this.selectedFilter === 'STUDENT') {
      this.getStudentUsers();
    } else if (this.selectedFilter === 'INSTRUCTOR') {
      this.getInstructorUsers();
    }
  }

  onFilterChange(event: Event) {
    this.selectedFilter = (event.target as HTMLSelectElement).value;
    this.applyFilter();
  }

  totalPages() {
    return this.userDetails.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.getDataBasedOnRole();
  }


}
