import {Component} from '@angular/core';
import {NgForOf, NgIf, CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ModalService} from '../../services/modal.service';
import {FeedbackRequest} from '../../interfaces/responses';
import {EnrollmentService} from '../../services/enrollment.service';
import {NotificationsComponent} from '../../shared-components/notifications/notifications.component';

@Component({
  selector: 'app-rating',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    FormsModule,
    CommonModule,
    NotificationsComponent,
  ],
  templateUrl: './rating.component.html',
  styleUrl: './rating.component.scss'
})
export class RatingComponent {
  courseId!: string;
  rating: number = 0;
  hoveredRating: number = 0;
  maxRating: number = 5;
  feedback: string = '';
  isRatingVisible: boolean = false;
  feedbackRequest: FeedbackRequest = {} as FeedbackRequest;
  notification = {
    show: false,
    message: '',
    type: '' as 'success' | 'error'
  };

  constructor(private modalService: ModalService,
              private enrollmentService: EnrollmentService) {
    this.modalService.isRatingVisible$.subscribe(visible => {
      this.isRatingVisible = visible;
    });

    this.modalService.courseId$.subscribe(courseId => {
      if (courseId) {
        this.courseId = courseId;
      }
    });
  }

  get stars(): number[] {
    return Array(this.maxRating).fill(0);
  }

  setHoveredRating(value: number) {
    this.hoveredRating = value;
  }

  resetHoveredRating() {
    this.hoveredRating = 0;
  }

  setRating(value: number) {
    this.rating = value;
  }

  submitRating() {
    if (!this.courseId) {
      console.error("Course ID is missing!");
      return;
    }
    this.feedbackRequest = {
      comment: this.feedback,
      rating: this.rating
    };
    this.enrollmentService.submitRating(this.courseId, this.feedbackRequest).subscribe({
        next: () => {
          this.notification = {
            show: true,
            message: 'Rating submitted successfully!',
            type: 'success'
          }
          setTimeout(() => {
            this.hideNotification()
          }, 3000);
        },
        error: (error) => {
          this.notification = {
            show: true,
            message: 'Failed to submit rating!',
            type: 'error'
          }
          setTimeout(() => {
            this.hideNotification()
          }, 3000);
        }
      }
    )
    setTimeout(() => {
      alert('Rating submitted successfully!');
      this.modalService.hideRatingModal();
    }, 1000);
  }

  closeRatingPopup() {
    this.modalService.hideRatingModal();
  }

  hideNotification() {
    this.notification = {
      show: false,
      message: '',
      type: 'success'
    }
  }
}

