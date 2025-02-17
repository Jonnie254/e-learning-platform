import { Component } from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router, RouterLink, RouterOutlet} from '@angular/router';
import {NgClass, NgIf} from '@angular/common';

@Component({
  selector: 'app-instructor-courses',
  standalone: true,
  imports: [
    RouterLink,
    RouterOutlet,
    NgClass,
  ],
  templateUrl: './instructor-courses.component.html',
  styleUrl: './instructor-courses.component.scss'
})
export class InstructorCoursesComponent {
  isManageCourseRoute: boolean = false;
  buttonText: string = 'Add Course';

  constructor(private router: Router, private route: ActivatedRoute) {
    this.route.url.subscribe({
      next: (url) => {
        this.router.events.subscribe((event) => {
          if (event instanceof NavigationEnd) {
            this.isManageCourseRoute = this.router.url.includes('/dashboard/instructor-courses/manage-course');
            this.buttonText = this.isManageCourseRoute ? 'Go Back to Courses' : 'Add Course';
          }
        });
      }
    });
  }

}
