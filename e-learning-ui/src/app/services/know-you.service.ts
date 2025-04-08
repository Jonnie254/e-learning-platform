import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from './auth-service.service';
import {KnowYouRequest, KnowYouResponse} from '../interfaces/responses';


@Injectable({
  providedIn: 'root'
})
export class KnowYouService {
  baseUrl: string = 'http://localhost:8222/api/v1/users';

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) { }

  getUserKnowAboutYou() {
    const token = this.authService.getToken();
    return this.httpClient.get<KnowYouResponse>(`${this.baseUrl}/get-user-know-you`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  checkWhetherUserHasKnowYou() {
    const token = this.authService.getToken();
    return this.httpClient.get(`${this.baseUrl}/check-user-know-you`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  addKnowAboutYou(formData: KnowYouRequest) {
    const token = this.authService.getToken();
    return this.httpClient.post(`${this.baseUrl}/create-know-you`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  updateKnowAboutYou(formData: KnowYouRequest, knowYouId: string) {
    const token = this.authService.getToken();
    return this.httpClient.put(`${this.baseUrl}/update-know-you/${knowYouId}`, formData, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
}
