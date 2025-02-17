import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {AuthService} from '../../services/auth-service.service';
import {loginUser} from '../../interfaces/users';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    RouterLink,
    NotificationsComponent,
    FormsModule,
    NgIf
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  loginSuccess: boolean = false;
  loginError: boolean = false;
  loginMessage: string = '';

  loginData: loginUser = {
    email: '',
    password: ''
  }
  loading: boolean = false;

  constructor(private router: Router, private authService: AuthService) {}

  onLogin() {
    if (this.loginData.email && this.loginData.password) {
      this.loading = true;
      this.authService.loginUser(this.loginData).subscribe({
        next: (res: any) => {
          if (res && res.token) {
            this.loginSuccess = true;
            this.loginError = false;
            this.loginMessage = 'Login successful';
          } else {
            this.loginSuccess = false;
            this.loginError = true;
            this.loginMessage = "Invalid login credentials";
          }
        },
        error: (err: any) => {
          this.loginSuccess = false;
          this.loginError = true;

          if (err.status === 401) {
            this.loginMessage = err.error.errors?.error || "Invalid email or password";
          } else if (err.status === 403) {
            this.loginMessage = "User is not active";
          } else {
            this.loginMessage = "An unexpected error occurred";
          }
        },
        complete: () => {
          this.loading = false;
          setTimeout(() => {
            this.loginSuccess = false;
            this.loginError = false;
            this.loginMessage = '';
          }, 5000);
        }
      });
    }
  }
}
