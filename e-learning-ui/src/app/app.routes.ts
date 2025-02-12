import { Routes } from '@angular/router';
import {LandingPageComponent} from './shared-components/landing-page/landing-page.component';
import {RegisterComponent} from './auth-pages/register/register.component';
import {LoginComponent} from './auth-pages/login/login.component';
import {ActivateAccountComponent} from './auth-pages/activate-account/activate-account.component';
import {CoursesComponent} from './student-pages/courses/courses.component';
import {CourseDetailsComponent} from './courses-pages/course-details/course-details.component';
import {CartDetailsComponent} from './courses-pages/cart-details/cart-details.component';
import {PaymentSuccessComponent} from './student-pages/payment-success/payment-success.component';


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
  },
  {
    path:'courses', component:CoursesComponent
  },
  {
    path:'courses/:courseId', loadComponent: () => import('./courses-pages/course-details/course-details.component').then(m => m.CourseDetailsComponent)
  },
  {
    path:'cart', loadComponent: () => import('./courses-pages/cart-details/cart-details.component').then(m => m.CartDetailsComponent)
  },
  {
    path:'payment-success', loadComponent: () => import('./student-pages/payment-success/payment-success.component').then(m => m.PaymentSuccessComponent)
  },
  {
    path:'my-courses', loadComponent: () => import('./student-pages/my-courses/my-courses.component').then(m => m.MyCoursesComponent)
  },
  {
    path:'enrolled-course/:courseId', loadComponent: () => import('./student-pages/enrolled-course/enrolled-course.component').then(m => m.EnrolledCourseComponent)
  },
  {
    path:'request-role', loadComponent: () => import('./auth-pages/request-role/request-role.component').then(m => m.RequestRoleComponent)
  }
];
