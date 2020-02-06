import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PaymentDTO} from '../shared/model/payment-dto.model';

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

  cancelReservation(reservationId: number) {
    return this._http.delete(`reservations/${reservationId}`);
  }

  payReservationCreatePayment(reservationId: number) {
    return this._http.post(`reservations/${reservationId}`, {});
  }

  payReservationExecutePayment(paymentDTO: PaymentDTO, reservationId: number) {
    return this._http.post(`reservations/${reservationId}/execute-payment`, paymentDTO.serialize());
  }
}
