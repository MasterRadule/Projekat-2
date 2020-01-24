import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Page} from '../../shared/model/page.model';
import {Reservation} from '../../shared/model/reservation.model';
import {MatSnackBar, PageEvent} from '@angular/material';
import {ReservationApiService} from '../../core/reservation-api.service';

@Component({
  selector: 'app-reservation-preview-list',
  templateUrl: './reservation-preview-list.component.html',
  styleUrls: ['./reservation-preview-list.component.scss']
})
export class ReservationPreviewListComponent implements OnInit {
  private _reservations: Reservation[];
  private _page: Page;
  private _reservationTypes: string[];
  @Output() reservationsPageChanged = new EventEmitter<Page>();
  @Output() resetPaginator = new EventEmitter<any>();
  private _reservationType = 'All';

  constructor(private _reservationApiService: ReservationApiService, private snackBar: MatSnackBar) {
    this._reservationTypes = ['All', 'Reserved', 'Bought'];
  }

  ngOnInit() {
    this._page = new Page();
    this._page.number = 0;
    this._page.size = 6;
    this.getReservations();
  }

  public getReservations() {
    this._reservationApiService.getReservations(this._reservationType, this._page.number, this._page.size).subscribe({
      next: (result: Page) => {
        this._page = result;
        this.reservationsPageChanged.emit(result);
        this._reservations = result.content;
      },
      error: (message: string) => {
        this.snackBar.open(message, 'Dismiss', {
          duration: 3000
        });
      }
    });
  }

  public pageChanged(event: PageEvent) {
    this._page.size = event.pageSize;
    this._page.number = event.pageIndex;
    this.getReservations();
  }
}
