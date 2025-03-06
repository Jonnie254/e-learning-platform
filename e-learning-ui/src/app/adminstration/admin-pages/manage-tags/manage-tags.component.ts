import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ModalService} from '../../../services/modal.service';

@Component({
  selector: 'app-manage-tags',
  standalone: true,
  imports: [
    RouterOutlet
  ],
  templateUrl: './manage-tags.component.html',
  styleUrl: './manage-tags.component.scss'
})
export class ManageTagsComponent {
  constructor(private modalService: ModalService) {}
  openModal() {
    this.modalService.showModal();
  }

}
