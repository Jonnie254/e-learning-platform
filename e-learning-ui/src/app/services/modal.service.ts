import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
  private isVisibleSource = new BehaviorSubject<boolean>(false);
  isVisible$ = this.isVisibleSource.asObservable();

  showModal() {
    this.isVisibleSource.next(true);
  }

  hideModal() {
    this.isVisibleSource.next(false);
  }
}
