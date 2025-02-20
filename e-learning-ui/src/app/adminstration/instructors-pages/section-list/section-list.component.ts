import { Component } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CoursesService} from '../../../services/courses-service.service';
import {InstructorCourseSectionResponse, PageResponse} from '../../../interfaces/responses';
import {NgForOf, NgIf} from '@angular/common';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';

@Component({
  selector: 'app-section-list',
  standalone: true,
  imports: [
    NgForOf,
    PaginationComponent,
    NgIf
  ],
  templateUrl: './section-list.component.html',
  styleUrl: './section-list.component.scss'
})
export class SectionListComponent {
  courseId: string = '';
  courseName: string = '';
  page: number = 0;
  size: number = 5;
  section: PageResponse<InstructorCourseSectionResponse> = { content: [] };

  constructor(
    private coursesService: CoursesService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.getCourseDetails();
  }

  getCourseDetails() {
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('courseId') as string;
      if (this.courseId) {
        this.getCourseSections();
      }
    });

    this.route.queryParamMap.subscribe(params => {
      this.courseName = params.get('courseName') || 'Unknown Course';
    });
  }

  getCourseSections(courseId: string = this.courseId, page: number = this.page, size: number = this.size) {
    if (!courseId) {
      return;
    }

    this.coursesService.getAllSectionsDetailsForCourse(courseId, { size, page }).subscribe({
      next: (response) => {
        this.section = response && response.content ? response : { content: [] };
      },
      error: (error) => {
        console.error('Error fetching course sections:', error);
      }
    });
  }

  totalPages() {
    return this.section.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.getCourseSections();
  }

  navigateToAddSection() {
    this.router.navigate([
      '/dashboard/instructor-courses/manage-section',
      this.courseId, 'new'
    ], { queryParams: { courseName: this.courseName } });
  }
  navigateToUpdateSection(section: InstructorCourseSectionResponse) {
    this.router.navigate([
      '/dashboard/instructor-courses/manage-section',
      this.courseId, section.sectionId
    ], { queryParams: { courseName: this.courseName } });
  }

}
