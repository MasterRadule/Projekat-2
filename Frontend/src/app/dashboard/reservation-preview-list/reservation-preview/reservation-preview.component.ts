import {Component, Input, OnInit} from '@angular/core';
import {Reservation} from '../../../shared/model/reservation.model';

@Component({
  selector: 'app-reservation-preview',
  templateUrl: './reservation-preview.component.html',
  styleUrls: ['./reservation-preview.component.scss']
})
export class ReservationPreviewComponent implements OnInit {
  @Input() private reservation: Reservation;

  constructor() { }

  ngOnInit() {
  }

}
