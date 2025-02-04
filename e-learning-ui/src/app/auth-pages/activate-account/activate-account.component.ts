import { Component } from '@angular/core';
import {CodeInputModule} from 'angular-code-input';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth-service.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-activate-account',
  standalone: true,
  imports: [
    CodeInputModule,
    NgIf
  ],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.scss'
})
export class ActivateAccountComponent {
  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  onCodeCompleted(token: string) {
    this.submitted = true;
    this.confirmAccount(token);
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  confirmAccount(token: string) {
    this.authService.activateAccount(token).subscribe({
      next: () => {
        console.log(token);
        this.message = 'Your account has been activated successfully. You can proceed to the login page';
        this.isOkay = true;
        this.submitted = true;
      },
      error: () => {
        this.message = 'An error occurred while activating your account. Please try again later';
        this.isOkay = false;
        this.submitted = true;
      }
    });
  }

}
