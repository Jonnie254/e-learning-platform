import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {CoursesCardComponent} from '../../courses-pages/courses-card/courses-card.component';
import {PaginationComponent} from '../../shared-components/pagination/pagination.component';
import {CourseResponse, PageResponse} from '../../interfaces/responses';
import {CoursesService} from '../../services/courses-service.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [
    NavbarComponent,
    NgIf,
    CoursesCardComponent,
    PaginationComponent,
    NgForOf
  ],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent {
  page: number = 0;
  size: number = 8;
  coursesResponse: PageResponse<CourseResponse> = {};
  isDropdownOpen = false;

  constructor(private courseService: CoursesService,
              private router: Router) {
    this.getCourses();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  getStars() {
    // Implement logic for getting stars (if needed)
  }


   getCourses() {
    this.courseService.getAllCourses(
      {size: this.size
        ,page: this.page},
      this.size)
      .subscribe(response => {
        console.log('Courses response', response);
        this.coursesResponse = response;
      });
  }


  totalPages() {
    return this.coursesResponse.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.getCourses();
  }

  onCourseClick(courseId: string) {
    this.router.navigate(['/courses', courseId]);
  }
}
