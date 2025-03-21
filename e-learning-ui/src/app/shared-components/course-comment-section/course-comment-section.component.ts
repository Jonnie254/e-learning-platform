import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FeedbackResponse} from '../../interfaces/responses';
import {DatePipe, NgClass, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-course-comment-section',
  standalone: true,
  imports: [
    NgForOf,
    NgIf,
    DatePipe,
    NgClass
  ],
  templateUrl: './course-comment-section.component.html',
  styleUrl: './course-comment-section.component.scss'
})
export class CourseCommentSectionComponent {
  @Input() comments:FeedbackResponse[] = [];

  // Moved from parent component
  getStars(rating: number): number[] {
    const fullStars = Math.floor(rating);
    const emptyStars = 5 - fullStars;
    return [...Array(fullStars).fill(1), ...Array(emptyStars).fill(0)];
  }

  getRatingColor(rating: number): string {
    if (rating >= 4.5) return "text-green-600";
    if (rating >= 3) return "text-yellow-500";
    return "text-red-500";
  }
}
