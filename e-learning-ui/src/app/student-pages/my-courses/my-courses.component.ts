import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';

@Component({
  selector: 'app-my-courses',
  standalone: true,
  imports: [
    NavbarComponent
  ],
  templateUrl: './my-courses.component.html',
  styleUrl: './my-courses.component.scss'
})
export class MyCoursesComponent {

}
