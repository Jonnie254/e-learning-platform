import {Component} from '@angular/core';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';
import {InstructorCoursesResponse, PageResponse} from '../../../interfaces/responses';
import {CoursesService} from '../../../services/courses-service.service';
import {CurrencyPipe, NgForOf, NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-courselist',
  standalone: true,
  imports: [
    PaginationComponent,
    NgForOf,
    CurrencyPipe,
    NgIf
  ],
  templateUrl: './courselist.component.html',
  styleUrl: './courselist.component.scss'
})
export class CourselistComponent {
  page: number = 0;
  size: number = 5;
  courses: PageResponse<InstructorCoursesResponse> = {};
  constructor(
    private coursesService: CoursesService,
    private router: Router) {
    this.getInstructorCourses();
  }

  getInstructorCourses() {
    this.coursesService.getInstructorCourses({ size: this.size, page: this.page }).subscribe({
      next: (response) => {
        this.courses = response;
      },
    })
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.getInstructorCourses();
  }


  totalPages() {
   return this.courses.totalPages as number;
  }
  navigateToManageCourse(course: InstructorCoursesResponse) {
    this.router.navigate(['/dashboard/instructor-courses/manage-course', course.courseId]);
  }

  navigateToCourseSections(course: InstructorCoursesResponse) {
    this.router.navigate(
      ['/dashboard/instructor-courses/sections', course.courseId],
      { queryParams: { courseName: course.courseName } }
    );
  }


  navigateToAddCourse() {
    this.router.navigate(['/dashboard/instructor-courses/add']);
  }
}
