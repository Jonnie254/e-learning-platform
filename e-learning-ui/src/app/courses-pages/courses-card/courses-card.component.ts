import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CourseResponse} from '../../interfaces/responses';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-courses-card',
  standalone: true,
  imports: [
    CurrencyPipe
  ],
  templateUrl: './courses-card.component.html',
  styleUrl: './courses-card.component.scss'
})
export class CoursesCardComponent {
  private _course: CourseResponse = {
    courseId: '',
    courseName: '',
    courseUrlImage: '',
    instructorName: '',
    price: 0,
  };
  @Output() courseClick = new EventEmitter<string>();


  get course(): CourseResponse {
    return this._course;
  }

  @Input()
  set course(value: CourseResponse) {
    this._course = value;
  }

  onExplore(){
    this.courseClick.emit(this._course.courseId);
  }

}
