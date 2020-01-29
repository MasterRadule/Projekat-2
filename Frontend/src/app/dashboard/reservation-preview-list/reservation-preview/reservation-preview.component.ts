import {Component, Input, OnInit} from '@angular/core';
import {Reservation} from '../../../shared/model/reservation.model';

@Component({
  selector: 'app-reservation-preview',
  templateUrl: './reservation-preview.component.html',
  styleUrls: ['./reservation-preview.component.scss']
})
export class ReservationPreviewComponent implements OnInit {
  private _reservation: Reservation;
  private _ticketNumber: number;
  private _price: number;
  private _paid: string;

  get reservation(): Reservation {
    return this._reservation;
  }

  @Input()
  set reservation(value: Reservation) {
    this._reservation = value;
    this._ticketNumber = value.tickets.length;
    this._price = value.tickets.reduce<number>((sum, ticket) => sum + ticket.price, 0);
    this._paid = value.orderId === null ? 'yes' : 'no';
  }

  constructor() {
  }

  ngOnInit() {
  }

}
