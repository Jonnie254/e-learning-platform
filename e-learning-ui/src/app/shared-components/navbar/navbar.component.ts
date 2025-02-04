import { Component } from '@angular/core';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {RouterLink} from '@angular/router';
import {User} from '../../interfaces/users';
import {AuthService} from '../../services/auth-service.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgOptimizedImage,
    NgClass,
    RouterLink,
    NgIf
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  isMobileMenuOpen: boolean = false;
  isLoggedIn: boolean = false;
  isUserDropdownOpen: boolean = false;
  isLoginDropdownOpen: boolean = false;
  user:User = {} as User;

  constructor(
    private authService: AuthService
  ) {
    this.authService.AuthChanged$.subscribe((isLoggedIn) => {
      console.log('Auth changed:', isLoggedIn);
      this.isLoggedIn = isLoggedIn;
      if (isLoggedIn) {
        console.log('Loading user details...');
        this.loadUserDetails();
      }
    });
  }


  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;

  }

  toggleLoginDropdown() {
      this.isLoginDropdownOpen = !this.isLoginDropdownOpen

  }
  toggleUserDropdown() {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  private loadUserDetails() {
    this.authService.getUserDetails().subscribe({
      next: (res: User) => {
        console.log('User details:', res);
        this.user = res;
      },
      error: (err) => {
        console.error('Error loading user details:', err);
      }
    });
  }

  logOut() {
    this.authService.logout();
  }
}
