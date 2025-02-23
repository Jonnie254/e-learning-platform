import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {Observable} from 'rxjs';
import {AuthService} from '../../services/auth-service.service';
import {AsyncPipe, NgIf} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ConfirmationDialogComponent} from '../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {User} from '../../interfaces/users';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [
    NavbarComponent,
    NgIf,
    AsyncPipe,
    ReactiveFormsModule,
    ConfirmationDialogComponent,
    NotificationsComponent,
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss'
})
export class ProfilePageComponent {
  userRole$: Observable<string | null>;
  profileImageUrl: string = '';
  profileImageFileName: string = '';
  isEditMode: boolean = false;
  buttonText: string = 'Edit';
  spinnerVisible: boolean = false;
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalConfirmButtonText: string = '';
  modalIconClass: string = '';
  modalCancelButtonText: string = '';
  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };
  userData: User= {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    profilePicUrl:''

}
  profileForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl(''),
    confirmPassword: new FormControl('')
  });

  constructor(private authService: AuthService) {
    this.userRole$ = this.authService.userRole$;
    this.getUserData();
  }

  getUserData() {
    this.authService.getUserDetails().subscribe({
      next: (response) => {
        console.log("User", response)
        this.userData = response;
        this.profileForm.patchValue({
          firstName: this.userData.firstName,
          lastName: this.userData.lastName,
          email: this.userData.email,
          profilePicUrl: this.userData.profilePicUrl
        })
      },
      error: (error) => {
        console.error('Error fetching user data:', error);
      }
    });
  }
  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    this.buttonText = this.isEditMode ? 'Save' : 'Edit';
    if (this.isEditMode) {
      this.profileForm.get('password')?.clearValidators();
      this.profileForm.get('confirmPassword')?.clearValidators();
    } else {
      this.profileForm.patchValue({ password: '', confirmPassword: '' });
    }
    this.profileForm.get('password')?.updateValueAndValidity();
    this.profileForm.get('confirmPassword')?.updateValueAndValidity();
  }


  onSave() {
    this.updateUserData();
  }

  prepareFormData() {
    const formData = new FormData();
    formData.append('firstName', this.profileForm.value.firstName);
    formData.append('lastName', this.profileForm.value.lastName);
    formData.append('email', this.profileForm.value.email);
    if (this.profileForm.value.password) {
      formData.append('password', this.profileForm.value.password);
    }
    if (this.profileImageUrl) {
      formData.append('profilePic', this.profileImageUrl);
    }
    return formData;
  }

  updateUserData(){
    if(!this.profileForm.valid){
      this.notification = {
        show: true,
        message: 'Please fill all required requirements',
        type: "error"
      }
      return;
    }
    const formData = this.prepareFormData();
    this.spinnerVisible = true;
    this.authService.updateUserProfile(formData).subscribe({
      next: (response) => {
        this.spinnerVisible = false;
        this.notification = {
          show: true,
          message: 'Profile updated successfully',
          type: 'success'
        }
        this.isEditMode = false;
        this.buttonText = 'Edit';
        this.profileImageFileName = '';
        this.getUserData();
        setTimeout(() => {
          this.notification = {
            show: false,
            message: '',
            type: 'success'
          }
        }, 3000);
      },
      error: (error) => {
        this.spinnerVisible = false;
        this.notification = {
          show: true,
          message: 'Failed to update profile',
          type: 'error'
        }
      }
    });
  }

  getImagesUrl(imageUrl: any) {
    const file = imageUrl.target.files[0];
    if(!file) return;
    this.profileImageUrl = file;
    this.profileImageFileName = file.name;
  }

  onConfirmAction() {
    this.onSave();
  }

  confirmAction() {
    this.isConfirmationDialogVisible = true;
    this.modalTitle = 'Update Profile';
    this.modalMessage = 'Are you sure you want to update your profile?';
    this.modalConfirmButtonText = 'Yes';
    this.modalCancelButtonText = 'No';
    this.modalIconClass = 'pi pi-exclamation-triangle';
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
