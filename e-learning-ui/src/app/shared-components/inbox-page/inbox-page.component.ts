import { Component } from '@angular/core';

@Component({
  selector: 'app-inbox-page',
  standalone: true,
  imports: [],
  templateUrl: './inbox-page.component.html',
  styleUrl: './inbox-page.component.scss'
})
export class InboxPageComponent {
  selectedChatRoom: any;

  sendMessage() {
    
  }
}
