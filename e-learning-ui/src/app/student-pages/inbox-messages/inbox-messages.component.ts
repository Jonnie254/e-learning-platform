import { Component } from '@angular/core';
import {NavbarComponent} from '../../shared-components/navbar/navbar.component';
import {InboxPageComponent} from '../../shared-components/inbox-page/inbox-page.component';

@Component({
  selector: 'app-inbox-messages',
  standalone: true,
  imports: [
    NavbarComponent,
    InboxPageComponent
  ],
  templateUrl: './inbox-messages.component.html',
  styleUrl: './inbox-messages.component.scss'
})
export class InboxMessagesComponent {

}
