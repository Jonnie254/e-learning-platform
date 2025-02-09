import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS, provideHttpClient} from '@angular/common/http';
import {tokenInterceptor} from './interceptors/token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection
    ({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),{
      provide: HTTP_INTERCEPTORS,
      useValue: tokenInterceptor,
      multi: true
    }
  ]
};
