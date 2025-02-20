import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CoursesService} from '../../../services/courses-service.service';
import {
  ConfirmationDialogComponent
} from '../../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../../shared-components/notifications/notifications.component';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {InstructorCourseSectionResponse} from '../../../interfaces/responses';

@Component({
  selector: 'app-manage-section',
  standalone: true,
  imports: [
    ConfirmationDialogComponent,
    NotificationsComponent,
    ReactiveFormsModule,
    NgIf,
  ],
  templateUrl: './manage-section.component.html',
  styleUrl: './manage-section.component.scss'
})

export class ManageSectionComponent {
  courseId: string | null = '';
  sectionId: string | null = '';
  isUploading: boolean = false;
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

  sectionData ={
    sectionName: '',
    sectionDescription: '',
    pdfUrl: '',
    videoUrl: ''
  }

  addSectionForm: FormGroup = new FormGroup({
    sectionName: new FormControl('', [Validators.required]),
    sectionDescription: new FormControl('', [Validators.required]),
  });
  constructor(
    private coursesService: CoursesService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.getRouteParams();

  }

  getRouteParams() {
    this.route.paramMap.subscribe(params => {
      this.courseId = params.get('courseId');
      this.sectionId = params.get('sectionId');
      if (this.sectionId && this.sectionId !== 'new') {
        this.actionType = 'update';
        this.getSectionDetails();
      } else {
        this.actionType = 'add';
        this.sectionId = null;
      }
    });
    this.route.queryParamMap.subscribe(params => {
      this.courseName = params.get('courseName') || 'Unknown Course';
    });
  }

  onConfirmAction() {
    if(this.actionType === 'add'){
      this.addSectionData();
    } else {
      this.updateSectionData();
    }
  }

  confirmAction() {
    this.isConfirmationDialogVisible = true;
    if(this.actionType === 'add'){
      this.modalTitle = 'Confirm Add Section';
      this.modalMessage = 'Are you sure you want to add this section?';
    } else {
      this.modalTitle = 'Confirm Update Section';
      this.modalMessage = 'Are you sure you want to update this section?';
    }
    this.modalIconClass = 'pi pi-exclamation-triangle';
    this.modalConfirmButtonText = this.actionType === 'add' ? 'Add' : 'Update';
    this.modalCancelButtonText = 'Cancel';
  }

  closeModal() {
    this.isConfirmationDialogVisible = false;
  }

  prepareSectionData() {
    const formData = new FormData();
    formData.append('sectionName', this.addSectionForm.value.sectionName);
    formData.append('sectionDescription', this.addSectionForm.value.sectionDescription);

    if (this.selectedPdfFile) {
      formData.append('sectionPdf', this.selectedPdfFile);
    }
    if (this.selectedVideoFile) {
      formData.append('sectionVideo', this.selectedVideoFile);
    }
    return formData;
  }

  getSectionDetails() {
    if (!this.sectionId) {
      console.error("Section ID is missing");
      return;
    }

    this.coursesService.getSectionDetails(this.sectionId).subscribe({
      next: (response: InstructorCourseSectionResponse) => {
        if (response) {
          this.sectionData = response;
          this.addSectionForm.patchValue({
            sectionName: this.sectionData.sectionName,
            sectionDescription: this.sectionData.sectionDescription
          });
          if (this.sectionData.pdfUrl) {
            this.selectedPdfFileName = this.getFileNameFromUrl(this.sectionData.pdfUrl);
          }

          if (this.sectionData.videoUrl) {
            this.selectedVideoFileName = this.getFileNameFromUrl(this.sectionData.videoUrl);
          }
        } else {
          console.warn("Received empty section details");
        }
      },
      error: (error) => {
        console.error("Error fetching section details", error);
        this.notification = {
          show: true,
          message: "Failed to load section details",
          type: "error"
        };
      }
    });
  }

  addSectionData() {
    if(!this.addSectionForm.valid){
      this.notification = {
        show: true,
        message: 'Please fill all required fields',
        type: 'error'
      }
      return;
    }
    const formData = this.prepareSectionData()
    this.isUploading = true;
    this.coursesService.addSection(this.courseId as string, formData).subscribe({
      next: ()=>{
        this.handleSectionResponse('Section added successfully');
       this.isUploading = false;
       setTimeout(() =>{
       this.router.navigate(['/dashboard/instructor-courses/sections', this.courseId]);
      }, 3000);
      },
      error: (error) => {
        this.handleSectionError(error);
        this.isUploading = false;
      }
    });
  }

  updateSectionData() {
    if(!this.addSectionForm.valid){
      this.notification = {
        show: true,
        message: 'Please fill all required fields',
        type: 'error'
      }
      return;
    }
    const formData = this.prepareSectionData()
    this.isUploading = true;
    this.coursesService.updateSection(this.sectionId as string, formData).subscribe({
      next: ()=>{
        this.handleSectionResponse('Section updated successfully');
       this.isUploading = false;
       setTimeout(() => {
         this.router.navigate(['/dashboard/instructor-courses/sections', this.courseId]);
       }, 3000);
      },
      error: (error: any) => {
        this.handleSectionError(error);
        this.isUploading = false;
      }
    });

  }

   handleSectionResponse(successMessage: string) {
    this.notification = {
      show: true,
      message: successMessage,
      type: 'success'
    }
    this.addSectionForm.reset();
    this.selectedPdfFile = null;
    this.selectedVideoFile = null;
    setTimeout(() => this.closeNotification(), 3000);

  }

  private handleSectionError(error: any) {
    if(error.error.errors){
      this.notification = {
        show: true,
        message: 'An unexpected error occurred',
        type: 'error'
      }
    } else {
      this.notification = {
        show: true,
        message: 'An unexpected error occurred',
        type: 'error'
      }
    }
    setTimeout(() => this.closeNotification(), 3000);
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }

  onVideoUpload(event: any) {
    const file = event.target.files[0];
    if(!file) return;
    if(!['video/mp4', 'video/quicktime', 'video/avi', 'video/mpeg'].includes(file.type)){
      this.notification = {
        show: true,
        message: 'Invalid file type. Please upload a video file',
        type: 'error'
      }
      return;
    }
    this.selectedVideoFile = file;
    this.selectedVideoFileName = file.name;
  }

  onPdfUpload(event: any) {
    const file = event.target.files[0];
    if(!file) return;
    if(file.type !== 'application/pdf'){
      this.notification = {
        show: true,
        message: 'Invalid file type. Please upload a pdf file',
        type: 'error'
      }
      return;
    }
    this.selectedPdfFile = file;
    this.selectedPdfFileName = file.name;
  }

  getFileNameFromUrl(fileUrl: string): string {
    return fileUrl.split('/').pop() || '';
  }

}
