import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {EnrollmentService} from '../../services/enrollment.service';
import {enrollmentResponse} from '../../interfaces/responses';
import {Router, RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [
    NavbarComponent,
    RouterLink,
    NgIf,
    NgForOf
  ],
  templateUrl: './my-courses.component.html',
  styleUrl: './my-courses.component.scss'
})
export class MyCoursesComponent {
  userEnrollments: enrollmentResponse = {} as enrollmentResponse;


  constructor(
    private enrollmentService: EnrollmentService,
    private router: Router
  ) {
    this.getUserEnrollments();
  }

  getUserEnrollments() {
    this.enrollmentService.getEnrollments().subscribe({
      next: (response) => {
        console.log('User enrollments:', response);
        this.userEnrollments = response;
      },
      error: (error) => {
        console.error('Error fetching user enrollments:', error);
      }
    })
  }

  redirectToCourse(courseId: string) {
    this.router.navigate(['/enrolled-course', courseId]);

  }
}
