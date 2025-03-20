import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-course-comment-section',
  standalone: true,
  imports: [],
  templateUrl: './course-comment-section.component.html',
  styleUrl: './course-comment-section.component.scss'
})
export class CourseCommentSectionComponent {
  @Input() comments!:any[];
}
