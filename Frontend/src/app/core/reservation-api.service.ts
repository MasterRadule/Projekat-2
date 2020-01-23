import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReservationApiService {

  constructor(private _http: HttpClient) {
  }
  getReservations(reservationType: string, page: number, size: number) {
    return this._http.get(`reservations?page=${page}&size=${size}&type=${reservationType.toUpperCase()}`);
  }

  getReservation(reservationId: number) {
    return this._http.get(`reservations/${reservationId}`);
  }
}
