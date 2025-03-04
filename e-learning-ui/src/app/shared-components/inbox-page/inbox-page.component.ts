import {AfterViewChecked, Component, ElementRef, ViewChild} from '@angular/core';
import {ChatService} from '../../services/chat.service';
import {AuthService} from '../../services/auth-service.service';
import {first} from 'rxjs';
import SockJS from 'sockjs-client';
import {Client, Stomp} from '@stomp/stompjs';
import {NotificationResponse, PageResponse} from '../../interfaces/responses';
import {UserDetailsResponse} from '../../interfaces/users';
import {CoursesChatRoomResponse, MessageResponse} from '../../interfaces/chats';
import {DatePipe, NgClass, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-inbox-page',
  standalone: true,
  imports: [
    NgForOf,
    DatePipe,
    NgIf,
    NgClass,
    FormsModule
  ],
  templateUrl: './inbox-page.component.html',
  styleUrl: './inbox-page.component.scss'
})
export class InboxPageComponent implements AfterViewChecked {
  selectedChatRoom: CoursesChatRoomResponse | null = null;
  webSocketUrl = 'http://localhost:8060/ws';
  stompClient: Client | null = null;
  notificationSubscription: any;
  userId: string | null = null;
  messageContent: string = '';
  coursesChatRoomResponse: PageResponse<CoursesChatRoomResponse> = {} as PageResponse<CoursesChatRoomResponse>;
  chatRoomMessages: MessageResponse[] = [];
  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  isUserScrollingUp: boolean = false;


  constructor(private chatService: ChatService, private authService: AuthService) {
    this.getUserIdAndInitWebSocket();
    this.getUsersChatRooms();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  getUsersChatRooms() {
    this.chatService.getAllChatRooms().subscribe({
      next: (response) => {
        this.coursesChatRoomResponse = response;
      },
      error: (err) => console.error('Failed to get chat rooms:', err),
    });
  }

  isSelfMessage(message: MessageResponse): boolean {
    return message.senderId === this.userId;
  }

  getUserIdAndInitWebSocket() {
    this.authService.getUserDetails().pipe(first()).subscribe({
      next: (user: UserDetailsResponse) => {
        if (!user || !user.id) {
          return;
        }
        this.userId = user.id;
        this.initWebSocket();
      }
    });
  }

  sendMessage() {
    if (!this.selectedChatRoom || !this.messageContent) {
      return;
    }
    this.chatService.sendMessage({
      content: this.messageContent,
      type: 'TEXT',
      chatRoomId: this.selectedChatRoom.chatRoomId,
    }).subscribe({
      next: () => {
        this.messageContent = '';
      }
    });
  }

  initWebSocket() {
    if (!this.userId) {
      return;
    }
    const wsUrl = `${this.webSocketUrl}?userId=${this.userId}`;
    const socket = new SockJS(wsUrl);
    this.stompClient = Stomp.over(socket);
    this.stompClient.onConnect = (frame) => {
      if (!this.coursesChatRoomResponse?.content || this.coursesChatRoomResponse.content.length === 0) {
        setTimeout(() => this.initWebSocket(), 1000);
        return;
      }
      this.coursesChatRoomResponse.content.forEach(chatRoom => {
        const subscriptionPath = `/topic/chat.${chatRoom.chatRoomId}`;
        this.notificationSubscription = this.stompClient!.subscribe(subscriptionPath, (message) => {
          if (message.body) {
              const notification: NotificationResponse = JSON.parse(message.body);
              this.handleNotification(notification);
          }
        });
      });
    };
    this.stompClient.activate();
  }

  handleNotification(notification: NotificationResponse) {
    if (!notification) return;
    if (this.selectedChatRoom && this.selectedChatRoom.chatRoomId === notification.chatRoomId) {
      const newMessage: MessageResponse = {
        senderId: notification.senderId as string,
        content: notification.content as string,
        createdAt: new Date(),
        messageType: notification.notificationType as 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO',
        mediaFilePath: notification.mediaUrl as string,
      };
      if (notification.notificationType === 'MESSAGE') {
        this.selectedChatRoom.lastMessageResponse.lastMessage = notification.content!;
      } else if (notification.notificationType === 'IMAGE') {
        this.selectedChatRoom.lastMessageResponse.lastMessage = 'Attachment';
      }
      this.chatRoomMessages = [...this.chatRoomMessages, newMessage];
      this.scrollToBottom();
      this.updateMessageStatus(this.selectedChatRoom);
    } else {
      const destChat = this.coursesChatRoomResponse.content?.find(c => c.chatRoomId === notification.chatRoomId);
      if (destChat) {
        destChat.lastMessageResponse.lastMessage =
          notification.notificationType === 'MESSAGE' ? notification.content! : 'Attachment';
        destChat.lastMessageResponse.lastMessageTime = new Date();
        destChat.unreadCount! += 1;
      } else {
        const newChat: CoursesChatRoomResponse = {
          chatRoomId: notification.chatRoomId!,
          course: {
            courseId: notification.senderId!,
            courseName: notification.content!,
            courseImageUrl: notification.mediaUrl!,
            instructorName: notification.receiverIds![0],
          },
          lastMessageResponse: {
            lastMessage: notification.content!,
            lastMessageTime: new Date(),
          },
          unreadCount: 1,
        };
        this.coursesChatRoomResponse.content = [newChat, ...(this.coursesChatRoomResponse.content || [])];
      }
    }
  }

  wrapMessage(lastMessage: string | null | undefined): string {
    if (!lastMessage) {
      return '';
    }
    return lastMessage.length > 20 ? lastMessage.substring(0, 17) + '...' : lastMessage;
  }

  selectChatRoom(chatRoom: CoursesChatRoomResponse) {
    this.selectedChatRoom = chatRoom;
    this.fetchChatRoomMessages(chatRoom.chatRoomId);
    this.updateMessageStatus(chatRoom);
  }

  updateMessageStatus(chatRoom: CoursesChatRoomResponse) {
    this.selectedChatRoom = chatRoom;
    this.chatService.updateMessageStatus(chatRoom.chatRoomId).subscribe({
      next: () =>{
        chatRoom.unreadCount = 0;
      }
    })
  }

  fetchChatRoomMessages(chatRoomId: string) {
    this.chatService.getChatRoomMessages(chatRoomId).subscribe({
      next: (res) => {
        this.chatRoomMessages = res;
      }
    });
  }

  onScroll() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      this.isUserScrollingUp = div.scrollTop + div.clientHeight < div.scrollHeight - 10;
    }
  }
  scrollToBottom() {
    setTimeout(() => {
      if (this.scrollableDiv && !this.isUserScrollingUp) {
        const div = this.scrollableDiv.nativeElement;
        div.scrollTop = div.scrollHeight;
      }
    }, 100);
  }
}
