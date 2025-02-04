import { Routes } from '@angular/router';
import {LandingPageComponent} from './shared-components/landing-page/landing-page.component';
import {RegisterComponent} from './auth-pages/register/register.component';
import {LoginComponent} from './auth-pages/login/login.component';
import {ActivateAccountComponent} from './auth-pages/activate-account/activate-account.component';


export const routes: Routes = [
  {
    path:'', component:LandingPageComponent
  },
  {
    path:'register', component: RegisterComponent
  },
  {
    path:'login', component: LoginComponent
  },
  {
    path:'activate-account', component: ActivateAccountComponent
  },
  {
    path:'landing-page', component: LandingPageComponent
  }
];
