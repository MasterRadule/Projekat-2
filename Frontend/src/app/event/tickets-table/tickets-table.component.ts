import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NewReservation} from '../../shared/model/new-reservation.model';
import {NewTicketDetailed} from '../../shared/model/new-ticket-detailed.model';
import {NewTicket} from '../../shared/model/new-ticket.model';
import {MatSnackBar, MatTable} from '@angular/material';
import {ReservationApiService} from '../../core/reservation-api.service';
import {Reservation} from '../../shared/model/reservation.model';
import {PaymentDTO} from '../../shared/model/payment-dto.model';
import {NewReservationAndPaymentDTO} from '../../shared/model/new-reservation-and-payment.model';

@Component({
  selector: 'app-tickets-table',
  templateUrl: './tickets-table.component.html',
  styleUrls: ['./tickets-table.component.scss']
})
export class TicketsTableComponent implements OnInit {
  private displayedColumns = ['seatGroupName', 'rowNum', 'colNum', 'eventDay', 'price', 'remove'];

  @Input() private reservation: NewReservation;
  @Output() private reservationChange = new EventEmitter<NewReservation>();
  @Output() private reservationCreated = new EventEmitter<null>();


  constructor(private reservationApiService: ReservationApiService, private matSnackBar: MatSnackBar) {
  }

  ngOnInit() {
  }

  private getTotalPrice() {
    return this.reservation.tickets.map(t => (t as NewTicketDetailed).price).reduce((a, b) => a + b, 0);
  }

  private removeTicket(ticket: NewTicket) {
    this.reservation.tickets = this.reservation.tickets.filter(t => t !== ticket);
    this.reservationChange.emit(this.reservation);
  }

  private createReservation() {
    this.reservationApiService.createReservation(this.reservation).subscribe({
      next: () => {
        this.reservationCreated.emit();
        this.matSnackBar.open('Successfully created the reservation', 'Dismiss', {
          duration: 3000
        });
      },
      error: (message) => {
        this.reservationCreated.emit();
        this.matSnackBar.open(message.error, 'Dismiss', {
          duration: 3000
        });
      }
    });
  }

  private paymentFunction() {
    return () => this.reservationApiService.createAndPayReservationCreatePayment(this.reservation).toPromise()
      .then(
        (value: PaymentDTO) => value.paymentID
      ).catch(
        (message) => {
          this.matSnackBar.open(message.error, 'Dismiss', {
            duration: 3000
          });
          throw message;
        }
      );
  }

  private onAuthorizeFunction(data) {
    return () => this.reservationApiService
      .createAndPayReservationExecutePayment(
        new NewReservationAndPaymentDTO(this.reservation, new PaymentDTO(data.paymentID, data.payerID))).toPromise()
      .then((value: Reservation) => {
        this.reservation = new NewReservation();
        this.reservationChange.emit(this.reservation);
        this.reservationCreated.emit();
        this.matSnackBar.open('Successfully created and payed the reservation', 'Dismiss', {
          duration: 3000
        });
      })
      .catch((message) => {
        this.reservationCreated.emit();
        this.matSnackBar.open(message.error, 'Dismiss', {
          duration: 10000
        });
        throw message;
      });
  }
}
