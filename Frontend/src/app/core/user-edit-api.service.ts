import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserDTO} from '../shared/model/user-dto.model';
import {ChangePasswordDTO} from '../shared/model/change-password-dto.model';

@Injectable({
  providedIn: 'root'
})
export class UserEditApiService {
  private _headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Credentials': 'true'
  });

  constructor(private _http: HttpClient) {
  }

  getUser(): Observable<any>  {
    return this._http.get(`user`, {headers: this._headers, responseType: 'json'});
  }

  edit(userDTO: UserDTO) {
    return this._http.put(`user`, userDTO, {headers: this._headers});
  }

  changePassword(changePasswordDTO: ChangePasswordDTO) {
    return this._http.put(`user/updatePassword`, changePasswordDTO, {headers: this._headers});
  }
}
