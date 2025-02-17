import { Component } from '@angular/core';
import {
  ConfirmationDialogComponent
} from '../../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../../shared-components/notifications/notifications.component';

@Component({
  selector: 'app-manage-courses',
  standalone: true,
  imports: [
    ConfirmationDialogComponent,
    NotificationsComponent
  ],
  templateUrl: './manage-courses.component.html',
  styleUrl: './manage-courses.component.scss'
})
export class ManageCoursesComponent {
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  }


  confirmAction() {

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
