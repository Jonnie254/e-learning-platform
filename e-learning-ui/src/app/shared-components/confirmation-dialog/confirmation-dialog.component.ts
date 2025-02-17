import {Component, EventEmitter, Input, Output} from '@angular/core';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './confirmation-dialog.component.html',
  styleUrl: './confirmation-dialog.component.scss'
})
export class ConfirmationDialogComponent {

  @Input() isVisible: boolean = false;
  @Input() title: string = ''
  @Input() message: string = '';
  @Input() confirmButtonText: string = 'Yes';
  @Input() cancelButtonText: string = 'No';
  @Input() iconClass: string = '';

  @Output() onConfirm = new EventEmitter();
  @Output() onCancel = new EventEmitter();
  @Output() onClose = new EventEmitter();

  closeModal(){
    this.onClose.emit();
    this.resetValues();
  }

  confirmAction(){
    this.onConfirm.emit();
    this.resetValues();
  }

  resetValues(){
    this.title = '';
    this.message = '';
    this.iconClass = '';
    this.confirmButtonText = 'Yes';
    this.cancelButtonText = 'No';
    this.isVisible = false;
  }

}
