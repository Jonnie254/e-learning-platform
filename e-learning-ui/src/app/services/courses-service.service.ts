import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CourseDetailsResponse, CourseResponse, PageResponse} from '../interfaces/responses';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {
  private baseUrl: string = 'http://localhost:8222/api/v1/courses';

  constructor(
    private httpClient: HttpClient
  ) {}
  // method to get all courses
  getAllCourses(page: { size: number; page: number }, size: number) {
    return this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/all-courses`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      }
    });
  }
   //method to get course by id
  getCourseById(courseId: string) {
    return this.httpClient.get<CourseDetailsResponse>(`${this.baseUrl}/${courseId}`);
  }

}
