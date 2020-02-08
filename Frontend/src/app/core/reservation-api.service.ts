import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PaymentDTO} from '../shared/model/payment-dto.model';
import {NewReservation} from '../shared/model/new-reservation.model';
import {NewReservationAndPaymentDTO} from '../shared/model/new-reservation-and-payment.model';

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

  createAndPayReservationCreatePayment(newReservation: NewReservation) {
    newReservation.tickets = newReservation.tickets.map(t => t.serialize());
    return this._http.post(`reservations/create-and-pay`, newReservation.serialize());
  }

  createAndPayReservationExecutePayment(newReservationAndPaymentDTO: NewReservationAndPaymentDTO) {
    newReservationAndPaymentDTO.paymentDTO = newReservationAndPaymentDTO.paymentDTO.serialize();
    newReservationAndPaymentDTO.newReservationDTO.tickets = newReservationAndPaymentDTO.newReservationDTO.tickets.map(t => t.serialize());
    newReservationAndPaymentDTO.newReservationDTO = newReservationAndPaymentDTO.newReservationDTO.serialize();
    return this._http.post(`reservations/create-and-pay/execute`, newReservationAndPaymentDTO.serialize());
  }

  createReservation(newReservation: NewReservation) {
    newReservation.tickets = newReservation.tickets.map(t => t.serialize());
    return this._http.post(`reservations`, newReservation.serialize());
  }
}
