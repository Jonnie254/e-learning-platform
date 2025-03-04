import { Injectable } from '@angular/core';
import {AuthService} from './auth-service.service';
import {PageResponse} from '../interfaces/responses';
import {CoursesChatRoomResponse, MessageResponse} from '../interfaces/chats';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private chatUrl: String = 'http://localhost:8222/api/v1/chats';

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) {
  }

  getAllChatRooms() {
    const token = this.authService.getToken();
    return this.http.get<PageResponse<CoursesChatRoomResponse>>(`${this.chatUrl}/get-chat-rooms`, {
      headers: {Authorization: `Bearer ${token}`}
    });
  }

  getChatRoomMessages(chatId: string) {
    const token = this.authService.getToken();
    return this.http.get<MessageResponse[]>(`${this.chatUrl}/get-messages/${chatId}`, {
      headers:
        {Authorization: `Bearer ${token}`}
    });
  }

  sendMessage(message: { content: string; type: string; chatRoomId: string }) {
    const token = this.authService.getToken();
    return this.http.post<MessageResponse>(`${this.chatUrl}/save-message`, message, {
      headers: {Authorization: `Bearer ${token}`}
    });
  }

  updateMessageStatus(chatRoomId: string) {
    const token = this.authService.getToken();
    return this.http.put(`${this.chatUrl}/update-message-status/${chatRoomId}`, null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}

