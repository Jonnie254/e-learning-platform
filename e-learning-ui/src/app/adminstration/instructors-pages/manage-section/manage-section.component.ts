import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CoursesService} from '../../../services/courses-service.service';
import {
  ConfirmationDialogComponent
} from '../../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../../shared-components/notifications/notifications.component';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-manage-section',
  standalone: true,
  imports: [
    ConfirmationDialogComponent,
    NotificationsComponent,
    ReactiveFormsModule,
  ],
  templateUrl: './manage-section.component.html',
  styleUrl: './manage-section.component.scss'
})

export class ManageSectionComponent {
  courseId: string | null = '';
  sectionId: string | null = '';
  courseName: string = '';
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string  = '';
  modalCancelButtonText:string = '';
  selectedPdfFile: File | null = null;
  selectedPdfFileName: string = '';
  selectedVideoFile: File | null = null;
  selectedVideoFileName: string = '';
  actionType: 'update' | 'add' = 'add';

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };


  constructor(
    private coursesService: CoursesService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.getRouteParams();
  }

 getRouteParams(){
    this.route.paramMap.subscribe({
      next:(params) =>{
        this.sectionId = params.get('sectionId') as string;
        this.courseId = params.get('courseId') as string;
        if(this.sectionId){
          this.actionType = 'update';
          this.courseId = null;
      } else {
        this.actionType = 'add';}
        this.sectionId = null;
      }
    });
   this.route.queryParamMap.subscribe(params => {
     this.courseName = params.get('courseName') || 'Unknown Course';
   });
  }

  onConfirmAction() {

  }

  closeModal() {
    this.isConfirmationDialogVisible = false;
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }
}
