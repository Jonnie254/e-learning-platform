export interface MessageRequest {
  content: string,
  type: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  chatRoomId: string;
}


export interface CoursesChatRoomResponse {
  chatRoomId:string;
  course : CourseChatResponse;
  lastMessageResponse: LastMessageResponse;
  unreadCount: number;
}


export interface CourseChatResponse {
  courseId: string;
  courseName: string;
  courseImageUrl: string;
  instructorName: string;
}

export interface  LastMessageResponse{
  lastMessage: string;
  lastMessageTime: Date;
}

export interface MessageResponse {
  messageId?: string;
  content: string;
  messageType?: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  notificationType?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  senderId: string;
  senderName?: string;
  mediaFilePath: string;
  createdAt: Date;
  messageStatuses?: MessageStatusResponse[];
}

export interface MessageStatusResponse {
  messageStatusId: string;
  recipientId: string;
  messageStatusType: 'SENT' | 'READ';
}

