import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth-service.service';
import {map} from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  return authService.userRole$.pipe(
    map(role =>{
      if(!role){
        router.navigate(['/login']);
        return false;
      }
      const allowedRoles = route.data?.['allowedRoles'] as string[];
      if(allowedRoles.includes(role)){
        return true;
      }else{
        router.navigate(['/login']);
        return false;
      }
    })
  )

};
