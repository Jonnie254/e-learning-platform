import { Component } from '@angular/core';
import {Route, Router, RouterLink} from '@angular/router';
import {registerUser} from '../../interfaces/users';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {FormsModule, NgForm} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../services/auth-service.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    RouterLink,
    NotificationsComponent,
    FormsModule,
    NgIf,
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  showNotification: boolean = false;
  notificationMessage: string = '';
  notificationType: 'success' | 'error' = 'success';

  registerData: registerUser = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: ''
  }
  confirmPassword: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  hideNotification() {
    this.showNotification = false;
  }

  autoHideError(registerForm: NgForm) {
    setTimeout(() => {
      registerForm.control.setErrors(null);
    }, 3000)
  }

  onRegister(registerForm: NgForm) {
    if (this.registerData.password !== this.confirmPassword) {
      this.notificationType = 'error';
      this.notificationMessage = 'Passwords do not match';
      this.showNotification = true;
      this.autoHideError(registerForm);
      return;
    }

    this.authService.registerUser(this.registerData).subscribe({
      next: () => {
        this.notificationType = 'success';
        this.notificationMessage = 'User registered successfully';
        this.showNotification = true;
        this.autoHideError(registerForm);
        setTimeout(() => {
          this.router.navigate(['activate-account']);
        }, 3000);
      }
    });
  }
}
