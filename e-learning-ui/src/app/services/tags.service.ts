import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthService} from './auth-service.service';

@Injectable({
  providedIn: 'root'
})
export class TagsService {

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) {
  }
}
