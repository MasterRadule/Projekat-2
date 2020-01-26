import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {LoginDto} from '../shared/model/login-dto.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationApiService {

  private _headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Credentials': 'true'
  });


  constructor(private _http: HttpClient) {
  }

  login(loginDTO: LoginDto): Observable<any> {
    return this._http.post(`login`, {
      email: loginDTO.email,
      password: loginDTO.password
    }, {headers: this._headers, responseType: 'text'});
  }

  getToken(): string {
    return localStorage.getItem('token');
  }

  isLogged(): boolean {
    if (localStorage.getItem('token')) {
      return true;
    }
    return false;
  }

  logout(): Observable<any> {
    return this._http.get(`auth/logout`, {headers: this._headers, responseType: 'text'});
  }


}
