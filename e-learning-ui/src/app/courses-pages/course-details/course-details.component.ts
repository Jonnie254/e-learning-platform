import { Component } from '@angular/core';
import { NavbarComponent } from '../../shared-components/navbar/navbar.component';
import {CourseDetailsResponse, CourseResponse, FeedbackResponse, PageResponse} from '../../interfaces/responses';
import { ActivatedRoute, Router } from '@angular/router';
import { CoursesService } from '../../services/courses-service.service';
import {CurrencyPipe, NgForOf} from '@angular/common';
import { CoursesCardComponent } from '../courses-card/courses-card.component';
import {AuthService} from '../../services/auth-service.service';
import {EnrollmentService} from '../../services/enrollment.service';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {take} from 'rxjs';
import {CourseCommentSectionComponent } from '../../shared-components/course-comment-section/course-comment-section.component';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [
    NavbarComponent,
    CurrencyPipe,
    NgForOf,
    CoursesCardComponent,
    NotificationsComponent,
    CourseCommentSectionComponent,
  ],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent {
  courseId!: string;
  comments: FeedbackResponse[] = [];
  course: CourseDetailsResponse = {} as CourseDetailsResponse;

  coursesResponse: PageResponse<CourseResponse> = {content: [], totalPages: 0};
  page: number = 0;
  size: number = 8;
  addToSuccess: boolean = false;
  addMessage: string = '';
  addCourseError: boolean = false;
  errorMessage: string = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private courseService: CoursesService,
    private enrollmentService: EnrollmentService
  ) {
    this.route.params.subscribe(params => {
      this.courseId = params['courseId'];
      this.loadCourseDetails();
      this.getCourses();
      this.getCourseFeedback();
    });

  }

  loadCourseDetails() {
    this.courseService.getCourseById(this.courseId).subscribe(
      (course: CourseDetailsResponse) => {
        this.course = course;
      }
    );
  }

  getCourses() {
    this.courseService.getFilteredCourses({size: this.size, page: this.page}, this.size)
      .subscribe((response) => {
        this.coursesResponse.content = response.content?.filter(course => course.courseId !== this.courseId);
        this.coursesResponse.totalPages = response.totalPages;
      });
  }

  onCourseClick(courseId: string) {
    this.router.navigate(['/courses', courseId]);
  }


  addToCart(courseId: string) {
    if (!this.authService.isAuthenticated()) {
      this.authService.redirectUrl = this.router.url;
      this.router.navigate(['/login']);
      return;
    }
    this.resetAddToCartStatus();
    this.enrollmentService.addCartItem(courseId).pipe(take(1)).subscribe({
      next: () => {
        this.addToSuccess = true;
        this.addCourseError = false;
        this.addMessage = 'Course added to cart successfully';
        setTimeout(() => {
          this.resetAddToCartStatus();
        }, 3000);
      },
      error: (err: any) => {
        this.errorMessage = err.error?.error || 'An unexpected error occurred';
        this.addToSuccess = false;
        this.addCourseError = true;
        setTimeout(() => {
          this.resetAddToCartStatus();
        }, 3000);
      }

    });
  }

  // method to reset the status of the add to cart operation
  resetAddToCartStatus() {
    this.addToSuccess = false;
    this.addCourseError = false;
    this.addMessage = '';
    this.errorMessage = '';
  }

  getCourseFeedback() {
    this.enrollmentService.getCourseFeedback(this.courseId, {size: this.size, page: this.page})
      .subscribe({
        next: (response) => {
          this.comments = response.content || [];
        },
        error: (error) => {
          this.comments = [];
        }
      });
  }

  getStars(rating: number): number[] {
    const fullStars = Math.floor(rating);
    const emptyStars = 5 - fullStars;
    return [...Array(fullStars).fill(1), ...Array(emptyStars).fill(0)];
  }

  getRatingColor(rating: number): string {
    if (rating >= 4.5) return "text-green-600"; // High rating (Green)
    if (rating >= 3) return "text-yellow-500"; // Medium rating (Yellow)
    return "text-red-500"; // Low rating (Red)
  }
}
