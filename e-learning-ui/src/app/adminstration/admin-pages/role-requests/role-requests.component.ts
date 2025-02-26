import { Component } from '@angular/core';
import {CommonModule} from '@angular/common';
import {PageResponse} from '../../../interfaces/responses';
import {RoleRequestStatus, RoleResponse} from '../../../interfaces/users';
import {AnalyticsService} from '../../../services/analytics.service';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';
import {
  ConfirmationDialogComponent
} from '../../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../../shared-components/notifications/notifications.component';

@Component({
  selector: 'app-role-requests',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    ConfirmationDialogComponent,
    NotificationsComponent
  ],
  templateUrl: './role-requests.component.html',
  styleUrl: './role-requests.component.scss'
})
export class RoleRequestsComponent {
  RoleRequestStatus = RoleRequestStatus;
  roleRequests:PageResponse<RoleResponse> = {content: []};
  page:number = 0;
  size:number = 4;
  selectedFilter: string = 'PENDING';
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';
  userId: string = '';
  requestId: string = '';
  actionType: 'approve' | 'reject' = 'approve';
  validationErrors: { [key: string]: string } = {};
  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  }

  constructor(
    private analyticsService: AnalyticsService,
  ) {
    this.getRoleStatus();
  }

  getRoleStatus() {
    this.applyFilter();
  }

  private applyFilter() {
    if (this.selectedFilter === 'PENDING') {
      this.getRoleStatusRequests();
    } else if (this.selectedFilter === 'REJECTED') {
      this.getRejectedRequests();
    }
  }

  onFilterChange(event: Event) {
    this.selectedFilter = (event.target as HTMLSelectElement).value;
    this.applyFilter();
  }

  getRoleStatusRequests() {
    this.analyticsService.getRoleRequests({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) => {
        this.roleRequests = res;
      }
    });
  }

  getRejectedRequests() {
    this.analyticsService.getRejectedRoleRequests({
      size: this.size,
      page: this.page
    }).subscribe({
      next: (res) => {
        this.roleRequests = res;
      }
    });
  }

  onConfirmAction() {
    if(this.actionType === 'approve') {
      this.approveRequest();
    } else if (this.actionType === 'reject') {
      this.rejectRequest();
    }
  }

  approveRequest() {
    this.analyticsService.processRoleRequest(
      this.requestId, this.userId, 'approve').subscribe({
      next: (res) =>{
       this.handleSuccessResponse('Role request approved successfully');
        this.getRoleStatus();
      }, error: (err) => {
        this.handleErrorResponse(err);
      }
    });
  }


  rejectRequest(){
    this.analyticsService.processRoleRequest(
      this.requestId, this.userId, 'disapprove').subscribe({
      next: (res) =>{
        this.handleSuccessResponse('Role request rejected successfully');
        this.getRoleStatus();
      }, error: (err) => {
        this.handleErrorResponse(err);
      }
    });
  }

  handleSuccessResponse(successMessage: string) {
    this.notification = {
      show: true,
      message: successMessage,
      type: 'success'
    }
    setTimeout(() => this.closeNotification(), 3000);
  }

  handleErrorResponse(error: any) {
    if (error.error.errors) {
      this.validationErrors = error.error.errors;
    } else {
      this.notification = {
        show: true,
        message: 'An unexpected error occurred',
        type: 'error'
      }
    }
    setTimeout(() => this.closeNotification(), 3000);
  }


  confirmAction(actionType: 'approve' | 'reject', userId: string, requestId: string) {
    this.isConfirmationDialogVisible = true;
    this.actionType = actionType;
    this.userId = userId;
    this.requestId = requestId;
    if(this.actionType === 'approve') {
      this.modalTitle = 'Approve Role Request';
     this.modalConfirmButtonText = 'Approve';
      this.modalMessage = 'Are you sure you want to approve this role request?';
    } else if (this.actionType === 'reject') {
      this.modalTitle = 'Reject Role Request';
      this.modalMessage = 'Are you sure you want to reject this role request?';
      this.modalConfirmButtonText = 'Reject';
      this.modalCancelButtonText = 'Cancel';
    }
    this.modalIconClass = 'pi pi-exclamation-triangle';
    this.modalCancelButtonText = 'Cancel';

  }

  totalPages() {
    return this.roleRequests.totalPages as number;
  }

  onPageChange(newPage: number) {
    this.page = newPage;
    this.applyFilter();
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }

  closeModal() {
    this.isConfirmationDialogVisible = false;
    this.modalTitle = '';
    this.modalMessage = '';
    this.modalIconClass = '';
    this.modalConfirmButtonText = '';
    this.modalCancelButtonText = '';

  }
}
