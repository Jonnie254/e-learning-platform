import { HttpInterceptorFn } from '@angular/common/http';


export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
 const token = localStorage.getItem('token');
 console.log('Token:', token);
 if(token){
   const authReq = req.clone({
     headers: req.headers.set('Authorization', `Bearer ${token}`)
   });
    return next(authReq);
 }
  return next(req);
};
