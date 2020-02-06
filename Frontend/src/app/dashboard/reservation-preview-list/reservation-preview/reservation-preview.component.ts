import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Reservation} from '../../../shared/model/reservation.model';
import {ReservationApiService} from '../../../core/reservation-api.service';
import {MatSnackBar} from '@angular/material';
import {PaymentDTO} from '../../../shared/model/payment-dto.model';

declare const paypal: any;

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

  @Output() reservationCancelled = new EventEmitter<any>();

  get reservation(): Reservation {
    return this._reservation;
  }

  @Input()
  set reservation(value: Reservation) {
    this._reservation = value;
    this._ticketNumber = value.tickets.length;
    this._price = value.tickets.reduce<number>((sum, ticket) => sum + ticket.price, 0);
    this._paid = value.orderId === null ? 'no' : 'yes';
  }

  constructor(private reservationApiService: ReservationApiService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    if (this._reservation.orderId === null) {
      this.configPayPal();
    }
  }

  private configPayPal() {
    console.log(paypal);
    paypal.Button.render({
      style: {
        size: 'small',
        color: 'gold',
        shape: 'pill',
        label: 'pay',
      },
      env: 'sandbox',
      payment: (data, actions) => this.reservationApiService.payReservationCreatePayment(this.reservation.id).toPromise()
        .then(
          (value: PaymentDTO) => value.paymentID
        ).catch(
          (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
            throw message;
          }
        ),
      onAuthorize: (data, actions) =>
        this.reservationApiService
          .payReservationExecutePayment(new PaymentDTO(data.paymentID, data.payerID), this.reservation.id).toPromise()
          .then((value: Reservation) => this.reservation = value)
          .catch((message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 10000
            });
            throw message;
          })
    }, `#pay-button-${this.reservation.id}`);
  }

  private cancelReservation(reservation: Reservation) {
    this.reservationApiService.cancelReservation(reservation.id).subscribe({
      next: (value: Reservation) => {
        this.reservation = value;
        this.snackBar.open('Successfully cancelled the reservation', 'Dismiss', {
          duration: 3000
        });
        this.reservationCancelled.emit();
      },
      error: (message: string) => {
        this.snackBar.open(message, 'Dismiss', {
          duration: 3000
        });
      }
    });
  }

}
