import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {Observable} from 'rxjs';
import {AuthService} from '../../services/auth-service.service';
import {AsyncPipe, NgIf} from '@angular/common';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    NavbarComponent,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss'
})
export class ProfilePageComponent {
  userRole$: Observable<string | null>;
  constructor(private authService: AuthService) {
    this.userRole$ = this.authService.userRole$;
  }

}
