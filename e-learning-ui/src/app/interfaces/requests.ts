export interface CourseRequest {
  courseName: string;
  courseDescription: string;
  coursePrice: string;
  whatYouWillLearn: string[];
  courseSelectedTags: string[];
  courseSelectedCategory: string;
}

export interface SectionRequest {
  sectionId?: string;
  sectionName: string;
  sectionDescription: string;
}
