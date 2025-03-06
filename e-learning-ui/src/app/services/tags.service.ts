import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth-service.service';
import {CategoryRequest, TagRequest, TagResponse} from '../interfaces/responses';

@Injectable({
  providedIn: 'root'
})
export class TagsService {
  baseUrl: string = 'http://localhost:8222/api/v1/courses';


  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {
  }

  addCategory(inputValue: CategoryRequest) {
    const token = this.authService.getToken();
    return this.httpClient.post(`${this.baseUrl}/create-category`, inputValue, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
  addTag(newTag: TagRequest) {
    const token = this.authService.getToken();
    return this.httpClient.post(`${this.baseUrl}/create-tag`, newTag, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }

  updateTag(tagId: string, tagResponse: TagRequest) {
    const token = this.authService.getToken();
    return this.httpClient.put(`${this.baseUrl}/update-tag/${tagId}`, tagResponse, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  updateCategory(categoryId: string, updatedCategory: CategoryRequest) {
    const token = this.authService.getToken();
    return this.httpClient.put(`${this.baseUrl}/update-category/${categoryId}`, updatedCategory, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });

  }
}
