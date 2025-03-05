import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ModalService} from '../../../services/modal.service';

@Component({
  selector: 'app-manage-courses',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  templateUrl: './manage-courses.component.html',
  styleUrl: './manage-courses.component.scss'
})
export class ManageCoursesComponent {
  constructor(private modalService: ModalService) {}

  openModal() {
    this.modalService.showModal();
  }
}
