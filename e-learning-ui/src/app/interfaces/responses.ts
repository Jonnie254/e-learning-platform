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

export interface enrollmentResponse {
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
