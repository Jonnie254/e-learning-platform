export interface PageResponse<T> {
  content?: T[];
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  last?: boolean;
  first?: boolean;
}

export interface CourseResponse {
  courseId: string;
  courseName: string;
  courseUrlImage: string;
  instructorName: string;
  price: number;
  description?: string,
  whatYouWillLearn?: string[],
  isInCart?: boolean;
  rating?: number;
}

export interface CourseDetailsResponse {
  courseId: string,
  courseName: string,
  courseImageUrl: string,
  instructorName: string,
  price: number,
  description: string,
  whatYouWillLearn: string[],
}

export interface Cart{
  cartId: string;
  totalAmount: number;
  reference: string;
  cartItems: cartItem[];
}

export interface cartItem {
  cartItemId: string;
  courseId: string;
  courseName: string;
  instructorName: string;
  courseImageUrl: string;
  price: number;

}

export interface EnrollmentResponse {
  content: courseEnrollment[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface courseEnrollment {
  enrollmentId: string;
  course: courseDetails;
}

export interface courseDetails {
  courseId: string;
  courseName: string;
  courseUrlImage: string;
  instructorName: string;
  progress: number;
}

export interface CourseSection {
  sectionId: string;
  sectionName: string;
  sectionDescription: string;
  pdfUrl: string;
  videoUrl: string;
  expanded?: boolean;
  isCompleted?: boolean;
}

export  interface SectionStatus {
  sectionId: string;
  isCompleted: boolean;
}

export interface TagResponse {
  tagId: string;
  tagName: string;
}

export interface CategoryResponse {
  categoryId: string;
  categoryName: string;
}

export interface InstructorCoursesResponse {
  courseId: string;
  courseName: string;
  courseImageUrl: string;
  price: number;
  category: string;
}

export interface InstructorFullCourseDetailsResponse{
  courseId: string;
  courseName: string;
  courseUrlImage: string;
  price: number;
  courseDescription: string;
  whatYouWillLearn: string[];
  tags: TagResponse[];
  category: CategoryResponse;
}

export interface InstructorCourseSectionResponse {
  sectionId: string;
  sectionName: string;
  courseName: string;
  sectionDescription: string;
  pdfUrl: string;
  videoUrl: string;
}

export interface NotificationResponse {
  content?: string;
  senderId?: string;
  receiverIds?: string[];
  chatRoomId?: string;
  messageType?: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  notificationType?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'VIDEO' | 'AUDIO';
  mediaUrl?: string;
}

export interface CategoryResponse{
  categoryId: string;
  categoryName: string;
}

export interface CategoryRequest{
  categoryId?: string;
  categoryName: string;
}

export interface TagRequest{
  tagId?: string;
  tagName: string;
}

export interface RatingResponse {
 progress: number;
 rated: boolean;
}

export interface FeedbackRequest{
  comment: string;
  rating: number;
}

export interface FeedbackResponse{
  feedbackId:string;
  userProfileResponse: UserProfileResponse;
  courseId:string;
  comment:string;
  rating: number;
  createdAt: Date;
}

export interface UserProfileResponse{
  id:string;
  firstName:string;
  lastName: string;
  profileImageUrl: string;
}

export interface CourseResponseRated{
  courseId: string;
  courseName:string;
  price: number;
  instructorId: string;
  instructorName: string;
  courseImageUrl: string;
  rating: number;
}
