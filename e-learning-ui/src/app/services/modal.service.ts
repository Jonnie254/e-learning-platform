import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
  private isVisibleSource = new BehaviorSubject<boolean>(false);
  isVisible$ = this.isVisibleSource.asObservable();
  private isRatingVisibleSource = new BehaviorSubject<boolean>(false);
  isRatingVisible$ = this.isRatingVisibleSource.asObservable();

  private courseIdSubject = new BehaviorSubject<string | null>(null);
  courseId$ = this.courseIdSubject.asObservable();


  showModal() {
    this.isVisibleSource.next(true);
  }

  hideModal() {
    this.isVisibleSource.next(false);
  }


  showRatingModal(courseId: string) {
    this.courseIdSubject.next(courseId);
    this.isRatingVisibleSource.next(true);
  }


  hideRatingModal() {
    this.isRatingVisibleSource.next(false);
  }
}
