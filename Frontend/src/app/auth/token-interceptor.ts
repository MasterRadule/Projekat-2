import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthenticationApiService} from "../core/authentication-api.service";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(public auth: AuthenticationApiService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.auth.getToken();
    let newHeaders = req.headers;

    if (token) {
      newHeaders = newHeaders.append('X-Auth-Token', token);
    }

    const authReq = req.clone({headers: newHeaders});
    return next.handle(authReq);
  }

}
