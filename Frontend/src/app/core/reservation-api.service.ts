import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = environment.baseUrl;
  }
  getReservations(reservationType: string, page: number, size: number) {
    return this._http.get(`${this._baseUrl}/reservations?page=${page}&size=${size}&type=${reservationType.toUpperCase()}`);
  }
}
