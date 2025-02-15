import { Component } from '@angular/core';
import {NavbarComponent} from '../navbar/navbar.component';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    NavbarComponent,
    RouterLink
  ],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {
}
