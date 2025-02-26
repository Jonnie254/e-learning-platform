import { Routes } from '@angular/router';
import {LandingPageComponent} from './shared-components/landing-page/landing-page.component';
import {RegisterComponent} from './auth-pages/register/register.component';
import {LoginComponent} from './auth-pages/login/login.component';
import {ActivateAccountComponent} from './auth-pages/activate-account/activate-account.component';
import {CoursesComponent} from './student-pages/courses/courses.component';
import {roleGuard} from './guards/role-guard.guard';
import {authGuard} from './guards/auth.guard';



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
   path:'my-inbox', loadComponent:() =>
      import('./student-pages/inbox-messages/inbox-messages.component')
        .then(m => m.InboxMessagesComponent),
  },
  {
    path:'courses/:courseId', loadComponent: () =>
      import('./courses-pages/course-details/course-details.component')
        .then(m => m.CourseDetailsComponent)
  },
  {
    path:'manage-profile', loadComponent:() =>
      import('./auth-pages/profile-page/profile-page.component')
        .then(m => m.ProfilePageComponent),

  },
  {
    path:'cart', loadComponent: () =>
      import('./courses-pages/cart-details/cart-details.component')
        .then(m => m.CartDetailsComponent),
      canActivate: [authGuard, roleGuard],
    data:{
      allowedRoles:['STUDENT']
    }
  },
  {
    path:'payment-success', loadComponent: () =>
      import('./student-pages/payment-success/payment-success.component')
        .then(m => m.PaymentSuccessComponent)
  },
  {
    path:'my-courses', loadComponent: () =>
      import('./student-pages/my-courses/my-courses.component')
        .then(m => m.MyCoursesComponent)
  },
  {
    path:'enrolled-course/:courseId', loadComponent: () =>
      import('./student-pages/enrolled-course/enrolled-course.component')
        .then(m => m.EnrolledCourseComponent),
    canActivate: [authGuard]
  },
  {
    path:'request-role', loadComponent: () =>
      import('./auth-pages/request-role/request-role.component')
        .then(m => m.RequestRoleComponent),
    canActivate: [authGuard]
  },
  {
    path:'profile',loadComponent:() =>
      import('./auth-pages/profile-page/profile-page.component')
        .then(m => m.ProfilePageComponent),
    canActivate: [authGuard]
  },
  {
    path:'dashboard',
    loadComponent: () =>
      import('./adminstration/dashboard-page/dash-page/dashboard-page.component')
        .then(m => m.DashboardPageComponent),
    children: [
      {
        path: '',
        redirectTo: 'analytics',
        pathMatch: 'full'
      },
      {
        path: 'analytics',
        loadComponent: () =>
          import('./adminstration/dashboard-page/analytics/analytics.component')
            .then(m => m.AnalyticsComponent),
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./adminstration/dashboard-page/analytics-page/analytics-page.component')
                .then(m => m.AnalyticsPageComponent)
          },
          {
            path: 'analytics-summary',
            loadComponent: () =>
              import('./adminstration/dashboard-page/analytics-page/analytics-page.component')
                .then(m => m.AnalyticsPageComponent)
          },
          {
            path: 'enrollment-summary',
            loadComponent: () =>
              import('./adminstration/dashboard-page/enrollment-summary/enrollment-summary.component')
                .then(m => m.EnrollmentSummaryComponent)
          },
          {
            path: 'revenue-summary',
            loadComponent:() =>
              import('./adminstration/dashboard-page/total-revenue-summary/total-revenue-summary.component')
                .then(m => m.TotalRevenueSummaryComponent)
          }
        ]
      },
      {
        path:'admin-courses',loadComponent:() =>
          import('./adminstration/admin-pages/manage-courses/manage-courses.component')
              .then(m => m.ManageCoursesComponent),
        canActivate: [roleGuard, authGuard],
        data:{
              allowedRoles:['ADMIN']
            }
      },
      {
        path:'instructor-courses',loadComponent:() =>
          import('./adminstration/instructors-pages/instructor-courses/instructor-courses.component')
            .then(m => m.InstructorCoursesComponent),
        children:[
          {
            path:'', loadComponent:() =>
              import('./adminstration/instructors-pages/courselist/courselist.component')
                .then(m => m.CourselistComponent),
          },
          {
            path:'course-list', loadComponent:() =>
              import('./adminstration/instructors-pages/courselist/courselist.component')
                .then(m => m.CourselistComponent),
          },
          {
            path: 'manage-course/:courseId',
            loadComponent: () =>
              import('./adminstration/instructors-pages/manage-course/manage-course.component')
                .then(m => m.ManageCourseComponent),
          },
          {
            path:'manage-course', loadComponent:() =>
              import('./adminstration/instructors-pages/manage-course/manage-course.component')
                .then(m => m.ManageCourseComponent),
          },
          {
            path:'sections/:courseId', loadComponent:() =>
              import('./adminstration/instructors-pages/section-list/section-list.component')
                .then(m => m.SectionListComponent),
          },
          {
            path: 'manage-section/:courseId/:sectionId',
            loadComponent: () =>
              import('./adminstration/instructors-pages/manage-section/manage-section.component')
                .then(m => m.ManageSectionComponent)
          }
        ]
        // canActivate: [roleGuard, authGuard],
        // data:{
        //   allowedRoles:['INSTRUCTOR']
        // }
      },
      {
        path:'profile',loadComponent:() =>
          import('./auth-pages/profile-page/profile-page.component')
            .then(m => m.ProfilePageComponent),
        canActivate: [authGuard]
      },
      {
        path:'users', loadComponent:() =>
          import('./adminstration/admin-pages/manage-users/manage-users.component')
            .then(m => m.ManageUsersComponent),
        // canActivate: [roleGuard, authGuard],
        // data:{
        //   allowedRoles:['ADMIN']
        // }
      },
      {
        path:'role-requests', loadComponent:() =>
          import('./adminstration/admin-pages/role-requests/role-requests.component')
            .then(m => m.RoleRequestsComponent),
      },
      {
        path:'inbox', loadComponent:() =>
          import('./shared-components/inbox-page/inbox-page.component')
            .then(m => m.InboxPageComponent),
        canActivate: [roleGuard, authGuard],
        data:{
          allowedRoles:['INSTRUCTOR']
        }
      }
    ]
  }
];
