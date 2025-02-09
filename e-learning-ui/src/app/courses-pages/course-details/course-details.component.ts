import { Component } from '@angular/core';
import { NavbarComponent } from '../../shared-components/navbar/navbar.component';
import { CourseDetailsResponse, CourseResponse, PageResponse } from '../../interfaces/responses';
import { ActivatedRoute, Router } from '@angular/router';
import { CoursesService } from '../../services/courses-service.service';
import { CurrencyPipe, NgForOf } from '@angular/common';
import { CoursesCardComponent } from '../courses-card/courses-card.component';
import {AuthService} from '../../services/auth-service.service';
import {EnrollmentService} from '../../services/enrollment.service';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [
    NavbarComponent,
    CurrencyPipe,
    NgForOf,
    CoursesCardComponent
  ],
  templateUrl: './course-details.component.html',
  styleUrl: './course-details.component.scss'
})
export class CourseDetailsComponent {
  courseId!: string;
  course: CourseDetailsResponse = {
    courseId: '',
    courseName: '',
    courseImageUrl: '',
    instructorName: '',
    price: 0,
    description: '',
    whatYouWillLearn: [],
  };

  coursesResponse: PageResponse<CourseResponse> = { content: [], totalPages: 0 };
  page: number = 0;
  size: number = 8;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private courseService: CoursesService,
    private enrollmentService: EnrollmentService
  ) {
    // Listen for route changes and update the course details
    this.route.params.subscribe(params => {
      this.courseId = params['courseId'];
      this.loadCourseDetails();
      this.getCourses();
    });

  }

  loadCourseDetails() {
    this.courseService.getCourseById(this.courseId).subscribe(
      (course: CourseDetailsResponse) => {
        console.log(course);
        this.course = course;
      }
    );
  }

  getCourses() {
    this.courseService.getAllCourses(
      { size: this.size, page: this.page },
      this.size
    ).subscribe(response => {
      this.coursesResponse.content = response.content?.filter(
        (course: CourseResponse) => course.courseId !== this.courseId
      );
      this.coursesResponse.totalPages = response.totalPages;
    });
  }

  onCourseClick(courseId: string) {
    this.router.navigate(['/courses', courseId]);
  }

  addToCart(courseId: string) {
    if(!this.authService.isAuthenticated()){
      this.authService.redirectUrl = this.router.url;
      this.router.navigate(['/login']);
    }else{
      this.enrollmentService.addCartItem(courseId).subscribe(
        () => {
          console.log('Course added to the cart');
        }
      );
    }
  }
}
