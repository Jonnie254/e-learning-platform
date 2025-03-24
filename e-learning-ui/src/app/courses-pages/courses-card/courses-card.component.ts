import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CourseResponse, CourseResponseRated} from '../../interfaces/responses';
import {CurrencyPipe, CommonModule} from '@angular/common';

@Component({
  selector: 'app-courses-card',
  standalone: true,
  imports: [
    CurrencyPipe, CommonModule
  ],
  templateUrl: './courses-card.component.html',
  styleUrl: './courses-card.component.scss'
})
export class CoursesCardComponent {
  private _course!: CourseResponse | CourseResponseRated;

  @Output() courseClick = new EventEmitter<string>();

  @Input()
  set course(value: CourseResponse | CourseResponseRated) {
    this._course = value;
  }

  @Input() isRatedCourse: boolean = false;

  get course(): CourseResponse | CourseResponseRated {
    return this._course;
  }

  getStars(rating: number | undefined): string[] {
    rating = rating ?? 0;
    const stars: string[] = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;
    const emptyStars = hasHalfStar ? 5 - fullStars - 1 : 5 - fullStars;

    for (let i = 0; i < fullStars; i++) stars.push("full");
    if (hasHalfStar) stars.push("half");
    for (let i = 0; i < emptyStars; i++) stars.push("empty");
    return stars;
  }

  get courseImage(): string {
    return (this._course as CourseResponse).courseUrlImage ||
      (this._course as CourseResponseRated).courseImageUrl || 'default-image.jpg';
  }

  onExplore() {
    this.courseClick.emit(this._course.courseId);
  }
}
