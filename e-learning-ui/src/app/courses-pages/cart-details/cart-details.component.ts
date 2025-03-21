import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {Router, RouterLink} from '@angular/router';
import {EnrollmentService} from '../../services/enrollment.service';
import {Cart} from '../../interfaces/responses';
import {CurrencyPipe, NgForOf, NgIf} from '@angular/common';
import {ConfirmationDialogComponent} from '../../shared-components/confirmation-dialog/confirmation-dialog.component';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';
import {ModalService} from '../../services/modal.service';
import {LoadingSpinnerComponent} from '../../shared-components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-cart-details',
  standalone: true,
  imports: [
    NavbarComponent,
    RouterLink,
    NgForOf,
    NgIf,
    CurrencyPipe,
    ConfirmationDialogComponent,
    NotificationsComponent,
    LoadingSpinnerComponent
  ],
  templateUrl: './cart-details.component.html',
  styleUrl: './cart-details.component.scss'
})
export class CartDetailsComponent {
  cart: Cart = {} as Cart;
  isConfirmationDialogVisible: boolean = false;
  modalTitle: string = '';
  modalMessage: string = '';
  modalIconClass: string = '';
  modalConfirmButtonText: string = '';
  modalCancelButtonText: string = '';
  itemToRemoveId: string = '';
  actionType: 'remove' | 'checkout' = 'remove';

  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };


  constructor(
    private enrollmentService: EnrollmentService,
    private modalService: ModalService
  ) {
    this.enrollmentService.cart$.subscribe((cart =>{
      this.cart = cart;
    }))
    this.enrollmentService.getCart();
  }


  removeItem(cartItemId: string) {
    this.enrollmentService.removeCartItem(cartItemId).subscribe({
      next: () => {
        this.notification = {
          show: true,
          message: 'Course removed successfully',
          type: 'success'
        };
        setTimeout(() => this.closeNotification(), 5000);
      },
      error: (error) => {
        console.error('Error removing item:', error);
        this.notification = {
          show: true,
          message: 'Error removing item',
          type: 'error'
        };
      }
    });
  }

  confirmAction() {
    if (this.actionType === 'remove' && this.itemToRemoveId) {
      this.removeItem(this.itemToRemoveId);
    } else if (this.actionType === 'checkout') {
      this.checkoutFromCart();
    } else {
      this.notification = {
        show: true,
        message: 'An error occurred while processing the action',
        type: 'error'
      };
    }
    setTimeout(() => {
      this.closeNotification();
    }, 3000);
  }


  showConfirmationDialog(courseId: string) {
    if (!courseId) {
      return;
    }
    this.itemToRemoveId = courseId;
    this.modalTitle = 'Remove Course from Cart';
    this.modalMessage = 'Are you sure you want to remove this item from your cart?';
    this.modalIconClass = 'pi pi-trash';
    this.modalConfirmButtonText = 'Yes, Remove';
    this.modalCancelButtonText = 'Cancel';
    this.isConfirmationDialogVisible = true;
  }


  showCheckoutDialog() {
    this.actionType = 'checkout';
    this.modalTitle = 'Checkout';
    this.modalMessage = 'Are you sure you want to checkout?';
    this.modalIconClass = 'pi pi-check';
    this.modalConfirmButtonText = 'Yes, Checkout';
    this.modalCancelButtonText = 'Cancel';
    this.isConfirmationDialogVisible = true;
  }

  checkoutFromCart() {
    this.modalService.showLoadingSpinner();
    this.enrollmentService.checkout().subscribe({
      next: (response) => {
        const approvalUrl = response.approvalUrl;
        if (approvalUrl) {
          window.location.href = approvalUrl;
        } else {
          this.notification = {
            show: true,
            message: "Invalid payment link received. Please try again.",
            type: "error"
          };
          this.modalService.hideLoadingSpinner();
        }
      },
      error: (error) => {
        this.notification = {
          show: true,
          message: "Error checking out. Please try again.",
          type: "error"
        };
        this.modalService.hideLoadingSpinner();
      }
    });
  }

  closeNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }

  resetConfirmationDialog() {
    this.isConfirmationDialogVisible = false;
    this.modalTitle = '';
    this.modalMessage = '';
    this.modalIconClass = '';
    this.modalConfirmButtonText = '';
    this.modalCancelButtonText = '';
    this.itemToRemoveId = '';
    this.actionType = 'remove';
  }

  closeModal() {
    this.resetConfirmationDialog();
  }

}
