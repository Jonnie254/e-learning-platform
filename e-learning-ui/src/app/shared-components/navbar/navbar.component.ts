import { Component } from '@angular/core';
import {NgClass, NgIf, NgOptimizedImage} from '@angular/common';
import {RouterLink} from '@angular/router';
import {User} from '../../interfaces/users';
import {AuthService} from '../../services/auth-service.service';
import {EnrollmentService} from '../../services/enrollment.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
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
  cartItemsCount: number = 0;

  constructor(
    private authService: AuthService,
    private enrollmentService: EnrollmentService
  ) {
    this.authService.AuthChanged$.subscribe((isLoggedIn) => {
      this.isLoggedIn = isLoggedIn;
      if (isLoggedIn) {
        this.loadUserDetails();
        this.getCart();
      }
    });
    this.enrollmentService.cart$.subscribe((cart) => {
      this.cartItemsCount = cart.cartItems.length;
    });
  }

  getCart() {
    this.enrollmentService.getCart()?.subscribe({
      next: (cart) => {
        if (cart && cart.cartItems) {
          this.cartItemsCount = cart.cartItems.length;
        }
      },
      error: (err) => {
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

   loadUserDetails() {
    this.authService.getUserDetails().subscribe({
      next: (res: User) => {
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
