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
export class InstructorCoursesComponent{
  isManageCourseRoute: boolean = false;
  buttonText: string = 'Add Course';

  constructor(
    private router: Router,
  ) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isManageCourseRoute = this.router.url.includes('/dashboard/instructor-courses/manage-course');
        this.buttonText = this.isManageCourseRoute ? 'Go Back to Courses' : 'Add Course';
      }
    });
  }

  toggleView(): void {
    if (this.isManageCourseRoute) {
      this.router.navigate(['/dashboard/instructor-courses']).then(r => {
        return;
      });
    } else {
      this.router.navigate(['/dashboard/instructor-courses/manage-course']).then(r => {
        return;
      });
    }
  }
}
