import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {CoursesCardComponent} from '../../courses-pages/courses-card/courses-card.component';
import {PaginationComponent} from '../../shared-components/pagination/pagination.component';
import {
  cartItem,
  CategoryResponse,
  CourseResponse,
  CourseResponseRated,
  PageResponse
} from '../../interfaces/responses';
import {CoursesService} from '../../services/courses-service.service';
import {Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {EnrollmentService} from '../../services/enrollment.service';

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
  categoryResponse: PageResponse<CategoryResponse> = {} as PageResponse<CategoryResponse>;
  ratedCoursesResponse: PageResponse<CourseResponseRated> = {} as PageResponse<CourseResponseRated>;
  isDropdownOpen: string | null = null;
  cartItems: CourseResponse[] = [];
  cartSubcription: Subscription = new Subscription();

  constructor(
    private courseService: CoursesService,
    private enrollmentService: EnrollmentService,
    private router: Router
  ) {
    this.getCourses();
    this.cartSubcription = this.enrollmentService.cart$.subscribe((cart) => {
      this.cartItems = this.mapCartItemsToCourseResponse(cart.cartItems);
      this.getCourses();
    });
    this.getCourses();
    this.getCategories();
    this.getTopRatedCourses();
  }

  getCategories() {
    if (this.categoryResponse.content?.length) return;
    this.courseService.getCategories({size: this.size, page: this.page})
      .subscribe({
        next: (response) => {
          this.categoryResponse = response;
        },
        error: (err) => {
          console.log(err);
        }
      })
  }

  getCourses() {
    this.courseService.getFilteredCourses({ size: this.size, page: this.page }, this.size)
      .subscribe(response => {
        this.coursesResponse = response;
      });
  }

  getTopRatedCourses() {
    this.courseService.getTopRatedCourses({size: this.size, page: this.page})
      .subscribe({
        next: (response) => {
          this.ratedCoursesResponse = response;
        },
        error: (err) => {
          console.log(err);
        }
      })
  }


  toggleDropdown(menu: string) {
    this.isDropdownOpen = this.isDropdownOpen === menu ? null : menu;
  }

  totalPages() {
    return this.coursesResponse.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.getCourses();
  }

  totalRatedCourse() {
    return this.ratedCoursesResponse.totalPages as number;
  }

  onTopRatedPageChange(ratedPage: number) {
    this.page = ratedPage;
    this.getTopRatedCourses();
  }

  onCourseClick(courseId: string) {
    this.router.navigate(['/courses', courseId]);
  }

  private mapCartItemsToCourseResponse(cartItems: cartItem[]): CourseResponse[] {
    return cartItems.map((cartItem) => {
      return {
        courseId: cartItem.courseId,
        courseName: cartItem.courseName,
        courseUrlImage: cartItem.courseImageUrl,
        instructorName: cartItem.instructorName,
        price: cartItem.price,
        isInCart: true,
      };
    });
  }

}
