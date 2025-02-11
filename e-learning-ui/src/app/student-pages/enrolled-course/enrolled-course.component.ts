import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {PaginationComponent} from '../../shared-components/pagination/pagination.component';
import {CourseSection, PageResponse} from '../../interfaces/responses';
import {CoursesService} from '../../services/courses-service.service';
import {ActivatedRoute, Router} from '@angular/router';
import {EnrollmentService} from '../../services/enrollment.service';
import {ConfirmationDialogComponent} from '../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';

@Component({
  selector: 'app-enrolled-course',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    NgIf,
    NgClass,
    PaginationComponent,
    ConfirmationDialogComponent,
    NotificationsComponent
  ],
  templateUrl: './enrolled-course.component.html',
  styleUrl: './enrolled-course.component.scss'
})
export class EnrolledCourseComponent {
  page: number = 0;
  size: number = 5;
  courseId!: string;
  courseSection: PageResponse<CourseSection> = {};

  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';
  itemSectionId: string = '';
  actionType: 'complete' | 'checkout' = 'complete';

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };


  constructor(
    private courseService: CoursesService,
    private activatedRoute: ActivatedRoute,
    private enrollmentService: EnrollmentService
  ) {
    this.activatedRoute.params.subscribe({
      next: params =>{
        this.courseId = params['courseId'];
        this.getCourseSections();
      }
    })
  }

   getCourseSections() {
    this.courseService.getCourseSections(this.courseId, { size: this.size, page: this.page })
      .subscribe(response => {
        this.courseSection = response;
        this.courseSection.content?.forEach(section => {
          section.expanded = false;
          this.getSectionStatus(section.sectionId);
        });
      });
   }

   getSectionStatus(sectionId: string){
    this.enrollmentService.getSectionStatus(sectionId).subscribe({
      next:(response =>{
        const section = this.courseSection.content?.find(section => section.sectionId === sectionId);
        if(section){
          section.isCompleted = response.isCompleted;
        }
      })
    });
   }

   totalPages() {
     return this.courseSection.totalPages as number;
   }

    onPageChange(newPage: number) {
    this.page = newPage;
    this.getCourseSections();
  }

  showConfirmationDialog(sectionId:string){
    this.isConfirmationDialogVisible = true;
    this.modalTitle = 'Complete the Section';
    this.modalMessage = 'Are you sure you want to complete this section?';
    this.modalIconClass = 'pi pi-check';
    this.modalConfirmButtonText = 'Yes';
    this.modalCancelButtonText = 'No';
    this.itemSectionId = sectionId;
    this.actionType = 'complete';
  }

  toggleCompletion(sectionId: string) {
    this.enrollmentService.completeSection(sectionId).subscribe({
      next: () =>{
        this.notification = {
          show: true,
          message: 'Section completed successfully',
          type: 'success'
        }
        setTimeout(() => this.closeNotification(), 5000);
        this.getSectionStatus(sectionId);
      },
      error: (error) => {
        console.error('Error completing section:', error);
        this.notification = {
          show: true,
          message: 'Error completing section',
          type: 'error'
        }
      }
    });
  }
  toggleSectionExpansion(section: CourseSection) {
    section.expanded = !section.expanded;
  }

  confirmAction() {
    if(this.actionType === 'complete' && this.itemSectionId){
      this.toggleCompletion(this.itemSectionId);
    }
    setTimeout(() => {
      this.closeModal();
      this.closeNotification();
    }, 3000);
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }

  }

  closeModal() {
    this.resetValues();
  }

  resetValues() {
    this.modalTitle = '';
    this.modalMessage = '';
    this.modalIconClass = '';
    this.modalConfirmButtonText = '';
    this.modalCancelButtonText = '';
    this.itemSectionId = '';
    this.isConfirmationDialogVisible = false;
  }
}
