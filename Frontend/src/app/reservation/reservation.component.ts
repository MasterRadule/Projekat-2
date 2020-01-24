import { Component, OnInit } from '@angular/core';
import {Reservation} from '../shared/model/reservation.model';
import {ActivatedRoute} from '@angular/router';
import {ReservationApiService} from '../core/reservation-api.service';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-reservation',
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.scss']
})
export class ReservationComponent implements OnInit {
  private reservation: Reservation = new Reservation(null, null, null, null, null);
  private _ticketNumber: number;
  private _price: number;
  private _paid: string;

  constructor(private route: ActivatedRoute, private reservationApiService: ReservationApiService, private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.getReservation(params.id);
      }
    });
  }

  getReservation(id: number){
    this.reservationApiService.getReservation(id).subscribe(
      {
        next: (result: Reservation) => {
          this.reservation = result;
          this._ticketNumber = result.tickets.length;
          this._price = result.tickets.reduce<number>((sum, ticket) => sum + ticket.price, 0);
          this._paid = result.orderId === null ? 'yes' : 'no';
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

}
