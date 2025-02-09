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

}

export interface cartItem {
  courseId: string;
   courseName: string;
   price: number;
  courseImageUrl: string;
  instructorName: string;
  instructorId: string;
  cart: Cart;

}


export interface cartItemResponse {
  cartItemId: string;
  courseId: string;
  courseName: string;
  instructorName: string;
  courseImageUrl: string;
  price: number;
}
