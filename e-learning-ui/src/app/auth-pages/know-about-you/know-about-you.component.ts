import {Component} from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {CategoryResponse, KnowYouResponse, PageResponse, SkillLevel, TagResponse} from '../../interfaces/responses';
import {CoursesService} from '../../services/courses-service.service';
import {AuthService} from '../../services/auth-service.service';
import {NgForOf, NgIf} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ModalService} from '../../services/modal.service';
import {ConfirmationDialogComponent} from '../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {KnowYouService} from '../../services/know-you.service';

@Component({
  selector: 'app-know-about-you',
  standalone: true,
  imports: [
    NavbarComponent,
    NgForOf,
    ReactiveFormsModule,
    ConfirmationDialogComponent,
    NotificationsComponent,
    NgIf
  ],
  templateUrl: './know-about-you.component.html',
  styleUrl: './know-about-you.component.scss'
})
export class KnowAboutYouComponent {
  userId: string = '';
  page: number = 0;
  size: number = 8;
  interestedTags: PageResponse<TagResponse> = {} as PageResponse<TagResponse>;
  interestedCategory: PageResponse<CategoryResponse> = {} as PageResponse<CategoryResponse>;
  knowYouResponse: KnowYouResponse = {} as KnowYouResponse;
  isConfirmationDialogVisible: boolean = false;
  isEditMode: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';
  actionType: 'update' | 'add' = 'add';
  validationErrors: { [key: string]: string } = {};


  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };
  preferredSkillLevel: SkillLevel[] = [
    SkillLevel.BEGINNER,
    SkillLevel.INTERMEDIATE,
    SkillLevel.ADVANCED,
  ];
  learningGoal: string[] = [
    'Learn a new skill',
    'Advance in my career',
    'Change careers',
    'Personal development',
    'Other'
  ];
  selectedTags: TagResponse[] = [];
  addKnowAboutYouForm: FormGroup = new FormGroup({
    learningGoal: new FormControl({ value: '', disabled: true }, [Validators.required]),
    preferredSkillLevel: new FormControl({ value: '', disabled: true }, [Validators.required]),
    interestedCategory: new FormControl({ value: '', disabled: true }, [Validators.required]),
    interestedTags: new FormControl({ value: [], disabled: true }, [Validators.required]),
  });

  constructor(
    private courseService: CoursesService,
    private authService: AuthService,
    private knowYouService: KnowYouService,
    private modalService: ModalService
  ) {
    this.initialize();
  }

  initialize() {
    this.getCategories();
    this.getTags();
    this.getUserId();
    this.checkWhetherUserHasKnowYou();
  }
  checkWhetherUserHasKnowYou() {
    this.knowYouService.checkWhetherUserHasKnowYou().subscribe({
      next: (res) => {
        if (res) {
          this.getUsersKnowYouResponse();
          this.actionType = 'update';
        } else {
          this.actionType = 'add';
        }
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
  getUsersKnowYouResponse() {
    this.knowYouService.getUserKnowAboutYou().subscribe({
      next: (res) => {
        this.knowYouResponse = res;
        const tagNames: string[] = (res.interestedTags || []).map(tag => tag.tagName); // Map to get an array of tag names
        this.selectedTags = res.interestedTags || [];
        this.addKnowAboutYouForm.patchValue({
          learningGoal: res.learningGoal,
          preferredSkillLevel: res.preferredSkillLevel,
          interestedCategory: res.interestedCategory,
          interestedTags: tagNames
        });
      },
      error: (err) => {
        console.error(err);
      }
    });
  }


  enableEditMode() {
    this.isEditMode = true;
    if (this.actionType !== 'update') {
      this.actionType = 'add';
    }
    this.addKnowAboutYouForm.get('learningGoal')?.enable();
    this.addKnowAboutYouForm.get('preferredSkillLevel')?.enable();
    this.addKnowAboutYouForm.get('interestedCategory')?.enable();
    this.addKnowAboutYouForm.get('interestedTags')?.enable();
  }
  onConfirmAction() {
    if (this.actionType === 'add') {
      this.addKnowAboutYou();
    } else {
      this.updateKnowAboutYou();
    }
  }
  confirmTheAction() {
    this.isConfirmationDialogVisible = true;
    if (this.actionType === 'add') {
      this.modalTitle = 'Confirm Know You Addition';
      this.modalMessage = 'Are you sure you want to fill the form?';
    } else if (this.actionType === 'update') {
      this.modalTitle = 'Confirm Know You Update';
      this.modalMessage = 'Are you sure you want to update this form?';
    }
    this.modalIconClass = 'pi pi-exclamation-triangle';
    this.modalConfirmButtonText = this.actionType === 'add' ? 'Add' : 'Update';
    this.modalCancelButtonText = 'Cancel';
  }
  getUserId() {
    this.authService.userId$.subscribe(
      (userId) => {
        this.userId = userId;
      }
    )
  }
  getTags() {
    this.courseService.getTags({page: this.page, size: this.size}).subscribe({
      next: (res) => {
        this.interestedTags = res;
      }
    })
  }
  getCategories() {
    this.courseService.getCategories({page: this.page, size: this.size}).subscribe({
      next: (res) => {
        this.interestedCategory = res;
      }
    })
  }
  addTag(event: Event) {
    const target = event.target as HTMLSelectElement;
    const tagId = target.value;
    if (!tagId) return;

    const selectedTag = this.interestedTags.content?.find(tag => tag.tagId === tagId);
    if (selectedTag && !this.selectedTags.some(tag => tag.tagId === selectedTag.tagId)) {
      this.selectedTags.push(selectedTag);
      this.addKnowAboutYouForm.get('interestedTags')?.setValue(this.selectedTags);
    }

    target.value = '';
  }

  removeTag(tag: TagResponse) {
    this.selectedTags = this.selectedTags.filter(t => t.tagId !== tag.tagId);
    this.addKnowAboutYouForm.get('interestedTags')?.setValue(this.selectedTags);
  }


  prepareFormData(): any {
    return {
      learningGoal: this.addKnowAboutYouForm.get('learningGoal')?.value,
      preferredSkillLevel: this.addKnowAboutYouForm.get('preferredSkillLevel')?.value,
      interestedCategory: this.addKnowAboutYouForm.get('interestedCategory')?.value,
      interestedTags: this.selectedTags
    };
  }

  addKnowAboutYou() {
    const formData = this.prepareFormData();
    this.knowYouService.addKnowAboutYou(formData).subscribe({
      next: (res) => {
        this.handleResponse("Add know you successfully")
        this.closeModal();
        this.getUsersKnowYouResponse();
        this.setEditMode();
      },
      error: (err) => {
        console.error(err);
        this.handleError("Something went wrong")
      }
    });

  }
  updateKnowAboutYou() {

  }

  handleResponse(successMessage: string) {
    this.notification = { show: true, message: successMessage, type: 'success' };
    setTimeout(() => this.closeNotification(), 3000);
  }

  handleError(error: any) {
    if (error.error.errors) {
      this.validationErrors = error.error.errors;
    } else {
      this.notification = { show: true, message: 'An unexpected error occurred',
        type: 'error' };
    }
    setTimeout(() => this.closeNotification(), 3000);
  }

  closeModal() {
    this.isConfirmationDialogVisible = false;
    this.modalTitle = '';
    this.modalMessage = '';
    this.modalIconClass = '';
    this.modalConfirmButtonText = '';
    this.modalCancelButtonText = '';
  }
  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }

  }
  setEditMode() {
    this.isEditMode = false;
    this.addKnowAboutYouForm.get('learningGoal')?.disable();
    this.addKnowAboutYouForm.get('preferredSkillLevel')?.disable();
    this.addKnowAboutYouForm.get('interestedCategory')?.disable();
    this.addKnowAboutYouForm.get('interestedTags')?.disable();
  }
}
