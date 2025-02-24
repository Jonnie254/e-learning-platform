export interface EnrollmentStatsResponse {
  totalEnrollments: number;
}

export interface CourseEnrollmentResponse {
  courseId: string;
  courseName: string;
  courseUrlImage: string;
  instructorName: string;
  enrollentCount: number;
}

export interface TotalRevenueStatsResponse {
  totalEarning: number;
}


export interface TotalCoursesResponse{
  totalCourses: number;
}

export interface CourseEarningResponse {
  courseDetailsResponse: CourseDetailsResponse;
  totalEarning: number;
}

export interface CourseDetailsResponse {
  courseId: string;
  courseName: string;
  courseUrlImage: string;
}
