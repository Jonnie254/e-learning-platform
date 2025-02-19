import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';


interface DialogData{
  title: string;
  message: string;
  confirmButtonText: string;
  cancelButtonText: string;
  iconClass: string;
}
@Injectable({
  providedIn: 'root'
})

export class ConfirmationDialogService {
  private dialogState = new BehaviorSubject<DialogData | null>(null);
  private confirmCallback: (() => void) | null = null;

  dialogState$ = this.dialogState.asObservable();

  openDialog(dialogData: DialogData, onConfirm: () => void): void {
    this.dialogState.next(dialogData);
    this.confirmCallback = onConfirm;
  }

  confirm(): void {
    if (this.confirmCallback) {
      this.confirmCallback();
    }
    this.dialogState.next(null);
  }

  cancel(): void {
    this.dialogState.next(null);
  }
}
