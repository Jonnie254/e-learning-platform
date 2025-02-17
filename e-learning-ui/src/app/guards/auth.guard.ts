import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth-service.service';
import {inject} from '@angular/core';
import {map} from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
 return authService.AuthChanged$.pipe(
   map(isAuthenticated => {
      if(!isAuthenticated){
        router.navigate(['/landing-page']);
        return false;
      }
      return true;
   })
 )
};
