export interface EnrollmentStatsResponse {
  totalEnrollments: number;
}

export interface CourseEnrollmentResponse {
  courseId: string;
  courseName: string;
  courseUrlImage: string;
  InstructorName: string;
  enrollentCount: number;
}
