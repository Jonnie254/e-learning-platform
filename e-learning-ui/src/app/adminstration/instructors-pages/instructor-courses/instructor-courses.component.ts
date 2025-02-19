import { Component } from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {NgClass,} from '@angular/common';


@Component({
  selector: 'app-instructor-courses',
  standalone: true,
  imports: [
    NgClass,
    RouterOutlet,
  ],
  templateUrl: './instructor-courses.component.html',
  styleUrl: './instructor-courses.component.scss'
})
export class InstructorCoursesComponent {
  isManageCourseRoute: boolean = false;
  isSectionListRoute: boolean = false;
  isManageSectionRoute: boolean = false;
  buttonText: string = 'Add Course';

  constructor(private router: Router) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const url = this.router.url;
        this.isManageCourseRoute = url.includes('/dashboard/instructor-courses/manage-course');
        this.isSectionListRoute = url.includes('/dashboard/instructor-courses/sections/');
        this.isManageSectionRoute = url.includes('/dashboard/instructor-courses/manage-section/');
        if (this.isManageCourseRoute) {
          this.buttonText = 'Go Back to Courses';
        } else if (this.isSectionListRoute) {
          this.buttonText = 'Go Back to Course List';
        } else if (this.isManageSectionRoute) {
          this.buttonText = 'Go Back to Course Sections';
        }
        else {
          this.buttonText = 'Add Course';
        }
      }
    });
  }

  toggleView(): void {
    if (this.isManageCourseRoute) {
      this.router.navigate(['/dashboard/instructor-courses']);
    } else if (this.isSectionListRoute) {
      this.router.navigate(['/dashboard/instructor-courses/manage-course']);
    } else if (this.isManageSectionRoute) {
      this.router.navigate(['/dashboard/instructor-courses']);
    } else {
      this.router.navigate(['/dashboard/instructor-courses/manage-course']);
    }
  }
}
