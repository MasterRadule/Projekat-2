import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {LoginDto} from "../shared/model/login-dto.model";
import {Observable} from "rxjs";
import {UserDTO} from "../shared/model/user-dto.model";

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

  getRole(): string{
    let token = localStorage.getItem('token');
    let role = "NO_ROLE";
    if(token!=null){
      let jwtData = token.split('.')[1];
      let decodedJwtJsonData = window.atob(jwtData);
      let decodedJwtData = JSON.parse(decodedJwtJsonData);
      role = decodedJwtData.role[0].authority;
    }
    return role;
  }

  logout(): Observable<any> {
    return this._http.get(`logout`, {headers: this._headers, responseType: 'text'});
  }

  register(userDTO: UserDTO): Observable<any>{
    return this._http.post(`register`, userDTO.serialize(), {headers: this._headers, responseType: 'text'});
  }

}
