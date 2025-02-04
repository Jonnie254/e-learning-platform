import { HttpInterceptorFn } from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth-service.service';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
 const token = localStorage.getItem('token');
 if(token){
   const authReq = req.clone({
     headers: req.headers.set('Authorization', `Bearer ${token}`)
   });
    return next(authReq);
 }
  return next(req);
};
