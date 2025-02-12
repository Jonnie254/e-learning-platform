import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {ConfirmationDialogComponent} from '../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {AuthService} from '../../services/auth-service.service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-request-role',
  standalone: true,
  imports: [
    NavbarComponent,
    NotificationsComponent,
    ConfirmationDialogComponent,
    FormsModule
  ],
  templateUrl: './request-role.component.html',
  styleUrl: './request-role.component.scss'
})
export class RequestRoleComponent {
  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';
  actionType: 'complete' | 'checkout' = 'complete';

  constructor(
    private authService: AuthService,
  ) {

  }
  closeNotification() {
    this.notification.show = false;
  }

  confirmAction() {
    this.isConfirmationDialogVisible = false;
    this.sendRequestToBecomeInstructor();
  }

  closeModal() {
    this.isConfirmationDialogVisible = false;
    this.resetValues();
  }

  onRequest() {
    this.isConfirmationDialogVisible = true;
    this.modalTitle = 'Confirm Request';
    this.modalMessage = 'Are you sure you want to request to become an instructor?';
    this.modalIconClass = 'pi pi-exclamation-triangle';
    this.modalConfirmButtonText = 'Yes, Proceed';
    this.modalCancelButtonText = 'Cancel';
  }

  sendRequestToBecomeInstructor() {
    this.authService.makeRoleRequest().subscribe({
      next: () => {
        this.notification = {
          show: true,
          message: 'Your request to become an instructor has been submitted successfully!',
          type: 'success'
        };
        setTimeout(() => {
          this.closeNotification();
        }, 3000);
      },
      error: (error) => {
        let errorMessage = 'Error submitting request. Please try again later.';

        if (error.status === 400 && error.error?.error) {
          errorMessage = error.error.error;
        }

        this.notification = {
          show: true,
          message: errorMessage,
          type: 'error'
        };
        setTimeout(() => {
          this.closeNotification();
        }, 3000);
      }
    });
  }

  resetValues() {
    this.modalTitle = '';
    this.modalMessage = '';
    this.modalIconClass = '';
    this.modalConfirmButtonText = '';
    this.modalCancelButtonText = '';
  }
}
