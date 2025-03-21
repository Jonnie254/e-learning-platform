import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FeedbackResponse} from '../../interfaces/responses';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-course-comment-section',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './course-comment-section.component.html',
  styleUrl: './course-comment-section.component.scss'
})
export class CourseCommentSectionComponent {
  @Input() comments:FeedbackResponse[] = [];
}
