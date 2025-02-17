import { Component } from '@angular/core';
import {PaginationComponent} from '../../../shared-components/pagination/pagination.component';

@Component({
  selector: 'app-courselist',
  standalone: true,
  imports: [
    PaginationComponent
  ],
  templateUrl: './courselist.component.html',
  styleUrl: './courselist.component.scss'
})
export class CourselistComponent {

}
