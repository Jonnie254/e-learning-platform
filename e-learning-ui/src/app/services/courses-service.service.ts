import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  CategoryResponse,
  CourseDetailsResponse,
  CourseResponse,
  CourseSection, InstructorCourseSectionResponse, InstructorCoursesResponse, InstructorFullCourseDetailsResponse,
  PageResponse, TagResponse
} from '../interfaces/responses';
import {EnrollmentService} from './enrollment.service';
import {map} from 'rxjs';
import {AuthService} from './auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class CoursesService {
  private baseUrl: string = 'http://localhost:8222/api/v1/courses';

  constructor(
    private httpClient: HttpClient,
    private enrollmentService: EnrollmentService,
    private authService: AuthService
  ) {}

  // method to get all courses from the backend
  getAllCourses(page: { size: number; page: number }, size: number) {
    return this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/all-courses`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      }
    });
  }
  // method to get a course by its id
  getCourseById(courseId: string) {
    return this.httpClient.get<CourseDetailsResponse>(`${this.baseUrl}/${courseId}`);
  }
  // method to get filtered courses
  getFilteredCourses(page: { size: number; page: number }, size: number) {
    const token = this.authService.getToken();
    const coursesObservable = token
      ? this.httpClient.get<PageResponse<CourseResponse>>(`${this.baseUrl}/available-for-user`, {
        params: {
          page: page.page.toString(),
          size: page.size.toString()
        },
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      : this.getAllCourses(page, size);

    return coursesObservable.pipe(
      map((response: PageResponse<CourseResponse>) => {
        const cartItems = this.enrollmentService.getCartItems();
        const filteredCourses = response.content?.filter(course =>
          !cartItems.some(cartItem => cartItem.courseId === course.courseId)
        ) || [];

        return {
          ...response,
          content: filteredCourses,
        };
      })
    );
  }
  //method to get courses sections
  getCourseSections(courseId: string, page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.httpClient.get<PageResponse<CourseSection>>(`${this.baseUrl}/${courseId}/sections`, {
      params:{
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  }
  //get the tags
  getTags(page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.httpClient.get<PageResponse<TagResponse>>(`${this.baseUrl}/tags`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //get all the categories
  getCategories(page: { size: number; page: number }) {
    return this.httpClient.get<PageResponse<CategoryResponse>>(`${this.baseUrl}/all-categories`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
    })
  }
  //add a course
  addCourse(formData: any){
    const token = this.authService.getToken();
    return this.httpClient.post(`${this.baseUrl}/create-course`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  // method to get the instructor courses
  getInstructorCourses(page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.httpClient.get<PageResponse<InstructorCoursesResponse>>(`${this.baseUrl}/instructor-courses`, {
      params: {
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //method to get full details for the course for the instructor
  getFullCourseDetails(courseId: string) {
    const token = this.authService.getToken();
    return this.httpClient.get<InstructorFullCourseDetailsResponse>(`${this.baseUrl}/instructor-course/${courseId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //method to update the course
  updateCourse(courseId: string, formData: any) {
    const token = this.authService.getToken();
    return this.httpClient.put(`${this.baseUrl}/update-course/${courseId}`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //method to get the course sections details for the instructor
  getAllSectionsDetailsForCourse(courseId: string, page: { size: number; page: number }) {
    const token = this.authService.getToken();
    return this.httpClient.get<PageResponse<InstructorCourseSectionResponse>>(`${this.baseUrl}/instructor-course-sections/${courseId}`, {
      params:{
        page: page.page.toString(),
        size: page.size.toString()
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //method to add a section
  addSection(courseId: string, formData: FormData) {
    const token = this.authService.getToken();
    return this.httpClient.post(`${this.baseUrl}/create-section-content/${courseId}`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  //method to get the section details
  getSectionDetails(sectionId: string | null) {
    const token = this.authService.getToken();
    return this.httpClient.get<InstructorCourseSectionResponse>(`${this.baseUrl}/section/${sectionId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }
  //method to update the section
  updateSection(sectionId: string, formData: FormData) {
    const token = this.authService.getToken();
    return this.httpClient.put(`${this.baseUrl}/section/update-content/${sectionId}`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      },
    });
  }
}
