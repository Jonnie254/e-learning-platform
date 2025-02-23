import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {NgForOf, NgIf} from '@angular/common';
import {CoursesCardComponent} from '../../courses-pages/courses-card/courses-card.component';
import {PaginationComponent} from '../../shared-components/pagination/pagination.component';
import {cartItem, CourseResponse, PageResponse} from '../../interfaces/responses';
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
  isDropdownOpen = false;
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
  }


  //get all courses
  getCourses() {
    this.courseService.getFilteredCourses({ size: this.size, page: this.page }, this.size)
      .subscribe(response => {
        this.coursesResponse = response;
      });
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  getStars() {
    // Implement logic for getting stars (if needed)
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
