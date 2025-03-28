import {Component, EventEmitter, Output} from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {EnrollmentService} from '../../services/enrollment.service';
import {Router, RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';
import {EnrollmentResponse} from '../../interfaces/responses';


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
  userEnrollments: EnrollmentResponse = {} as EnrollmentResponse;
  constructor(
    private enrollmentService: EnrollmentService,
    private router: Router
  ) {
    this.getUserEnrollments();
  }

  getUserEnrollments() {
    this.enrollmentService.getEnrollments().subscribe({
      next: (response) => {
        this.userEnrollments = response;
        this.userEnrollments.content.forEach((enrollment) => {
          this.getCourseProgress(enrollment.course.courseId);
        });
      }
    })
  }

  getCourseProgress(courseId: string){
    this.enrollmentService.getCourseProgess(courseId).subscribe({
      next: (response) =>{
        const course = this.userEnrollments.content.
        find((enrollment) => enrollment.course.courseId === courseId);
        if(course){
          course.course.progress = response.progress;
        }
      }
    });
  }

  redirectToCourse(courseId: string) {
    this.router.navigate(['/enrolled-course', courseId]);
  }

  progressCircleDashArray(progress: number): string {
    return `${progress * 0.628} 94.2`;
  }

  progressCircleDashOffset(progress: number): string {
    return `${94.2 - (progress * 0.628)}`;
  }
}
