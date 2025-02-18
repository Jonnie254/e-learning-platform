import {Component} from '@angular/core';
import {
    ConfirmationDialogComponent
} from "../../../shared-components/confirmation-dialog/confirmation-dialog.component";
import {NotificationsComponent} from "../../../shared-components/notifications/notifications.component";
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {CoursesService} from '../../../services/courses-service.service';
import {CategoryResponse, PageResponse, TagResponse} from '../../../interfaces/responses';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-manage-course',
  standalone: true,
  imports: [
    ConfirmationDialogComponent,
    NotificationsComponent,
    NgIf,
    NgClass,
    NgForOf,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './manage-course.component.html',
  styleUrl: './manage-course.component.scss'
})
export class ManageCourseComponent {
  courseId: string = '';
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string  = '';
  modalCancelButtonText:string = '';
  selectedFile: File | null = null;
  selectedFileName: string = '';
  actionType: 'update' | 'add' = 'add';
  currentStepIndex = 0;
  page: number = 0;
  size: number = 8;
  availableTags: PageResponse<TagResponse> = {content: []};
  availableCategories : PageResponse<CategoryResponse> = {content: []};
  validationErrors: { [key: string]: string } = {};

  steps = [
    { title: 'Course Info', status: 'completed' },
    { title: 'Additional Info', status: 'completed' },
    { title: 'Course Media', status: 'pending' }
  ];

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };

  addCourseForm: FormGroup = new FormGroup({
    courseName: new FormControl('', [Validators.required]),
    courseDescription: new FormControl('', [Validators.required]),
    coursePrice: new FormControl('', [Validators.required]),
    whatYouWillLearn: new FormControl([''], [Validators.required]),
    courseSelectedTags: new FormControl([], { nonNullable: true }),
    courseSelectedCategory: new FormControl('', [Validators.required])
  });

  courseData = {
    name: '',
    description: '',
    price: '',
    whatYouWillLearn: [''],
    selectedTags: [] as TagResponse[],
    selectedCategory: '',
    imageUrl: ''
  };

  constructor(
    private coursesService: CoursesService,
    private route: ActivatedRoute
    ) {
    this.getAvailableTags();
    this.getAllCategories();
    this.getCourseId();
  }

  getCourseId(){
    this.route.paramMap.subscribe({
      next: (params) =>{
        this.courseId = params.get('courseId') as string;
        if(this.courseId){
          this.actionType = 'update';
          this.getCourseDetails();
        } else{
          this.actionType = 'add';
        }
      }
    })
  }

  getCourseDetails(){
    this.coursesService.getFullCourseDetails(this.courseId).subscribe({
      next: (response) => {
        this.courseData = {
          name: response.courseName,
          description: response.courseDescription,
          price: response.price.toString(),
          whatYouWillLearn: response.whatYouWillLearn,
          selectedTags: response.tags,
          selectedCategory: response.category.categoryId,
          imageUrl: response.courseUrlImage
        };
        this.addCourseForm.patchValue({
          courseName: this.courseData.name,
          courseDescription: this.courseData.description,
          coursePrice: this.courseData.price,
          whatYouWillLearn: this.courseData.whatYouWillLearn,
          courseSelectedTags: this.courseData.selectedTags.map(tag => tag.tagId),
          courseSelectedCategory: this.courseData.selectedCategory
        });
      }
    });
  }

  getAvailableTags() {
    this.coursesService.getTags({size: this.size, page: this.page}).subscribe({
      next: (response) => {
        this.availableTags = response;
      }
    })
  }

  getAllCategories() {
    this.coursesService.getCategories({size: this.size, page: this.page}).subscribe({
      next: (response) => {
        this.availableCategories = response;
      }
    })
  }

  addLearningPoint() {
    if (this.courseData.whatYouWillLearn.length < 4) {
      this.courseData.whatYouWillLearn.push('');
    } else {
      this.notification = {
        show: true,
        message: 'You can only add up to 4 learning points',
        type: 'error'
      };
      setTimeout(() => {
        this.closeNotification();
      }, 3000);
    }
  }

  removeLearningPoint(index: number) {
    this.courseData.whatYouWillLearn.splice(index, 1);
  }

  addTag(event: Event) {
    const target = event.target as HTMLSelectElement;
    const tagId = target.value;
    if (!tagId) return;
    const selectedTag = this.availableTags.content?.find
    (tag => tag.tagId === tagId);
    if (selectedTag && !this.courseData.selectedTags.some
    (tag => tag.tagId === selectedTag.tagId)) {
      this.courseData.selectedTags.push(selectedTag);
    }
    target.value = '';
  }

  removeTag(tag: TagResponse) {
    this.courseData.selectedTags = this.courseData.selectedTags.filter
    (t => t.tagId !== tag.tagId);
  }

  onFileChange(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.selectedFileName = file.name;
    }
  }

  confirmAction() {
    console.log('Confirm action triggered');
    this.isConfirmationDialogVisible = true;
    console.log('Modal visibility:', this.isConfirmationDialogVisible);
    if (this.actionType === 'add') {
      this.modalTitle = 'Confirm Course Addition';
      this.modalMessage = 'Are you sure you want to add this course?';
    } else if (this.actionType === 'update') {
      this.modalTitle = 'Confirm Course Update';
      this.modalMessage = 'Are you sure you want to update this course?';
    }
    this.modalIconClass = 'pi pi-exclamation-triangle';
    this.modalConfirmButtonText = this.actionType === 'add' ? 'Add' : 'Update';
    this.modalCancelButtonText = 'Cancel';
  }

  addCourse() {
    console.log('Add Course button clicked');
    this.actionType = 'add';
    this.confirmAction();
  }

  updateCourse() {
    this.actionType = 'update';
    this.confirmAction();
  }

  onConfirmAction() {
    if (this.actionType === 'add') {
      this.addCourseData();
    } else if (this.actionType === 'update') {
     this.updateCourseData();
    }
    this.closeModal();
  }

  addCourseData() {
    if (!this.addCourseForm.valid) {
      this.notification = { show: true, message: 'Please fill in all required fields', type: 'error' };
      setTimeout(() => this.closeNotification(), 3000);
      return;
    }

    const formData = new FormData();
    formData.append('courseName', this.addCourseForm.value.courseName);
    formData.append('courseDescription', this.addCourseForm.value.courseDescription);
    formData.append('price', this.addCourseForm.value.coursePrice);
    formData.append('categoryId', this.addCourseForm.value.courseSelectedCategory);
    formData.append('whatYouWillLearn', JSON.stringify(this.addCourseForm.value.whatYouWillLearn));

    const selectedTags = Array.isArray(this.addCourseForm.value.courseSelectedTags)
      ? this.addCourseForm.value.courseSelectedTags
      : [this.addCourseForm.value.courseSelectedTags];

    selectedTags.forEach((tagId: string) => {
      formData.append('tagIds', tagId);
    });
    if (this.selectedFile) {
      formData.append('courseImage', this.selectedFile);
    }

    console.log('Form data:', formData);

    this.coursesService.addCourse(formData).subscribe({
      next: (response) => {
        this.notification = { show: true, message: 'Course added successfully', type: 'success' };
        this.validationErrors = {};
        this.addCourseForm.reset();
        this.selectedFile = null;
        setTimeout(() => this.closeNotification(), 3000);
      },
      error: (error) => {
        if (error.error.errors) {
          this.validationErrors = error.error.errors;
        } else {
          this.notification = { show: true, message: 'An unexpected error occurred', type: 'error' };
        }
        setTimeout(() => this.closeNotification(), 3000);
      }
    });
  }

  updateCourseData() {
    if (!this.addCourseForm.valid) {
      this.notification = { show: true, message: 'Please fill in all required fields', type: 'error' };
      setTimeout(() => this.closeNotification(), 3000);
      return;
    }

    const formData = new FormData();
    formData.append('courseName', this.addCourseForm.value.courseName);
    formData.append('courseDescription', this.addCourseForm.value.courseDescription);
    formData.append('price', this.addCourseForm.value.coursePrice);
    formData.append('categoryId', this.addCourseForm.value.courseSelectedCategory);
    formData.append('whatYouWillLearn', JSON.stringify(this.addCourseForm.value.whatYouWillLearn));

    const selectedTags = Array.isArray(this.addCourseForm.value.courseSelectedTags)
      ? this.addCourseForm.value.courseSelectedTags
      : [this.addCourseForm.value.courseSelectedTags];

    selectedTags.forEach((tagId: string) => {
      formData.append('tagIds', tagId);
    });

    if (this.selectedFile) {
      formData.append('courseImage', this.selectedFile);
    }

    console.log('Form data for update:', formData);

    this.coursesService.updateCourse(this.courseId, formData).subscribe({
      next: (response) => {
        this.notification = {show: true, message: 'Course updated successfully', type: 'success'};
        this.validationErrors = {};
        this.addCourseForm.reset();
        this.selectedFile = null;
        setTimeout(() => this.closeNotification(), 3000);
      },
      error: (error) => {
        if (error.error.errors) {
          this.validationErrors = error.error.errors;
        } else {
          this.notification = {show: true, message: 'An unexpected error occurred', type: 'error'};
        }
        setTimeout(() => this.closeNotification(), 3000);
      }
    });
  }

  nextStep() {
    if (this.currentStepIndex < this.steps.length - 1) {
      this.currentStepIndex++;
    } else if (this.currentStepIndex === this.steps.length - 1) {
      this.confirmAction();
    }
  }

  prevStep() {
    if (this.currentStepIndex > 0) {
      this.currentStepIndex--;
    }
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


