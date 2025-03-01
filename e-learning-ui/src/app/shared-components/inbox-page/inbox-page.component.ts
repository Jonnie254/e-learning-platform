import { Component } from '@angular/core';
import {ChatService} from '../../services/chat.service';
import {AuthService} from '../../services/auth-service.service';
import {first} from 'rxjs';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import {NotificationResponse} from '../../interfaces/responses';
import {UserDetailsResponse} from '../../interfaces/users';

@Component({
  selector: 'app-inbox-page',
  standalone: true,
  imports: [],
  templateUrl: './inbox-page.component.html',
  styleUrl: './inbox-page.component.scss'
})
export class InboxPageComponent {
  selectedChatRoom: any;
  private webSocketUrl = 'http://localhost:8060/ws';
  private stompClient: Client | null = null;
  private notificationSubscription: any;
  userId: string | null = null; // Store userId after fetching it

  constructor(
    private chatService: ChatService,
    private authService: AuthService
  ) {
    this.getUserIdAndInitWebSocket();
  }

  sendMessage() {
    // Add your message sending logic here
  }

  getUserIdAndInitWebSocket() {
    this.authService.getUserDetails().pipe(first()).subscribe({
      next: (user: UserDetailsResponse) => {
        if (!user || !user.id) {
          console.error('User ID is missing, cannot establish WebSocket connection.');
          return;
        }
        console.log('User details:', user);
        this.userId = user.id;
        this.initWebSocket();
      },
      error: (err) => {
        console.error('Failed to get user details', err);
      }
    });
  }

  initWebSocket() {
      if (!this.userId) {
        console.error('User ID is missing, cannot establish WebSocket connection.');
        return;
      }

      const wsUrl = `${this.webSocketUrl}?userId=${this.userId}`;
      const socket = new SockJS(wsUrl);

      this.stompClient = new Client({
        webSocketFactory: () => socket,
        debug: (msg) => console.log(msg),
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.stompClient.onConnect = () => {
        console.log('WebSocket connected');

        this.notificationSubscription = this.stompClient!.subscribe('/user/queue/chat', (message) => {
          const notification: NotificationResponse = JSON.parse(message.body);
          this.handleNotification(notification);
        });
      };

      this.stompClient.onStompError = (error) => {
        console.error('WebSocket error:', error);
      };

      this.stompClient.activate();
    }

    handleNotification(notification: NotificationResponse) {
      if (!notification) return;
      console.log('New notification:', notification);
    }
}
