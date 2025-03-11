import { Component } from '@angular/core';
import {AsyncPipe, NgIf} from "@angular/common";
import {ModalService} from '../../services/modal.service';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [
    NgIf,
    AsyncPipe
  ],
  templateUrl: './loading-spinner.component.html',
  styleUrl: './loading-spinner.component.scss'
})
export class LoadingSpinnerComponent {
  constructor(
    public modalService: ModalService) {

  }

}
