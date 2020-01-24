import {Component, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {EventPreviewListComponent} from './event-preview-list/event-preview-list.component';
import {LocationPreviewListComponent} from './location-preview-list/location-preview-list.component';
import {PaginatorComponent} from './paginator/paginator.component';
import {ReservationPreviewListComponent} from './reservation-preview-list/reservation-preview-list.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit, OnInit {
  @ViewChild(PaginatorComponent, {static: false}) paginator: PaginatorComponent;
  @ViewChild(EventPreviewListComponent, {static: false}) eventListComponent: EventPreviewListComponent;
  @ViewChild(LocationPreviewListComponent, {static: false}) locationListComponent: LocationPreviewListComponent;
  @ViewChild(ReservationPreviewListComponent, {static: false}) reservationListComponent: ReservationPreviewListComponent;
  private _content: string;

  constructor(private route: ActivatedRoute) {
  }

  get content(): string {
    return this._content;
  }

  set content(value: string) {
    this._content = value;
  }

  ngAfterViewInit() {
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this._content = params.get('content');
    });
  }

  private pageChanged($event) {
    switch (this._content) {
      case 'locations':
        this.locationListComponent.pageChanged($event);
        break;
      case 'events':
        this.eventListComponent.pageChanged($event);
        break;
      case 'reservations':
        this.reservationListComponent.pageChanged($event);
        break;
    }
  }

  private contentPageChanged($event) {
    this.paginator.page = $event;
  }
}
