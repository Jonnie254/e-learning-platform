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
    NotificationsComponent
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
    password: ''

}

  profileForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required])
  });

  constructor(private authService: AuthService) {
    this.userRole$ = this.authService.userRole$;
    this.getUserData();
  }


  getUserData() {
    console.log('getting user data');
    this.authService.getUserDetails().subscribe({
      next: (response) => {
        console.log('user data:', response);
        this.userData = response;
      }
    });
  }

  toggleEditMode() {
    this.isEditMode = !this.isEditMode;
    this.buttonText = this.isEditMode ? 'Save' : 'Edit';
  }


  onSave() {

  }

  getImagesUrl(imageUrl: any) {
    const file = imageUrl.target.files[0];
    if(!file) return;
    this.profileImageUrl = file;
    this.profileImageFileName = file.name;
  }

  onConfirmAction() {

  }

  closeModal() {

  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }
}
