import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@environments/environment';
import { AccountService } from '@app/_services';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
    constructor(private accountService: AccountService) { }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add auth header with jwt if user is logged in and request is to the api url
        const user = this.accountService.userValue;
        const creds = localStorage.getItem('creds');;
        const isLoggedIn = user && creds;
        const isProtectedUrl = request.url.startsWith('/rest/');

        if (isLoggedIn&&isProtectedUrl) {
            /*request = request.clone({
                setHeaders: {
                    creds: creds,
                    app_uid: user.email
                }
            });*/
            const modifiedReq = request.clone({
                          headers: request.headers.set('Authorization', creds).set('app_uid',user.email),
                        });
            return next.handle(modifiedReq);
        } else {
            return next.handle(request);
        }


    }
}
