import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AsyncPipe, NgClass, NgIf} from '@angular/common';
import {AuthService} from '../../../services/auth-service.service';
import {Observable} from 'rxjs';
import {User} from '../../../interfaces/users';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [
    RouterLink,
    NgIf,
    RouterOutlet,
    AsyncPipe,
    RouterLinkActive,
    NgClass
  ],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss'
})
export class DashboardPageComponent {
  isLoggedIn: boolean = false;
  isSidebarCollapsed: boolean = true;
  user: User = {} as User;
  isDropdownOpen = false;
  userRole$: Observable<string | null>;
  constructor(private authService: AuthService) {
    this.userRole$ = this.authService.userRole$;
    this.authService.AuthChanged$.subscribe({
      next: (isLoggedIn) => {
        this.isLoggedIn = isLoggedIn;
        if (isLoggedIn) {
          this.loadUserDetails();
        }
      }
    })
   }

   loadUserDetails() {
    this.authService.getUserDetails().subscribe(({
      next:(res: User) => {
        this.user = res;
      }
    }))
   }
  logout() {
    this.authService.logout();
  }
  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }
}
