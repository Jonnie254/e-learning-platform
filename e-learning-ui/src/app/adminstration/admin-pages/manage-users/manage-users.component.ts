import { Component } from '@angular/core';
import {NgClass, NgForOf} from '@angular/common';
import {User} from '../../../interfaces/users';
import {Observable} from 'rxjs';
import {AuthService} from '../../../services/auth-service.service';
import {AnalyticsService} from '../../../services/analytics.service';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [
    NgForOf,
    NgClass
  ],
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.scss'
})
export class ManageUsersComponent {
  users = [
    {
      id: 1,
      name: "John Doe",
      email: "john@example.com",
      role: "Admin",
      imageUrl: "https://via.placeholder.com/64",
    },
    {
      id: 2,
      name: "Jane Smith",
      email: "jane@example.com",
      role: "Editor",
      imageUrl: "https://via.placeholder.com/64",
    },
    {
      id: 3,
      name: "Alice Johnson",
      email: "alice@example.com",
      role: "Viewer",
      imageUrl: "https://via.placeholder.com/64",
    },
  ];
  userRole$: Observable<string | null>;
  userRole: string | null = '';
  page: number = 0;
  size: number = 4;

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
      this.getUsers();
    }

  }

  private getUsers() {

  }
}
