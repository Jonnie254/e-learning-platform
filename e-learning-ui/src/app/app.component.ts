import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {RegisterComponent} from './auth-pages/register/register.component';
import {LandingPageComponent} from './shared-components/landing-page/landing-page.component';
import {ConfirmationDialogComponent} from './shared-components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'e-learning-ui';
}
