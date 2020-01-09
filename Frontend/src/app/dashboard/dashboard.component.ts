import {Component, OnInit} from '@angular/core';
import {Page} from '../shared/model/page.model';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar, PageEvent} from '@angular/material';
import {Location} from '../shared/model/location.model';
import {EventApiService} from '../core/event-api.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private _locations: Location[];
  private _events: Event[];
  private _pageLocations: Page;
  private _pageEvents: Page;

  constructor(private _locationApiService: LocationApiService, private _eventApiService: EventApiService,
              private _snackBar: MatSnackBar) {
  }

  get pageLocations(): Page {
    return this._pageLocations;
  }

  set pageLocations(value: Page) {
    this._pageLocations = value;
  }

  get locations(): Location[] {
    return this._locations;
  }

  set locations(value: Location[]) {
    this._locations = value;
  }

  get events(): Event[] {
    return this._events;
  }

  set events(value: Event[]) {
    this._events = value;
  }

  get pageEvents(): Page {
    return this._pageEvents;
  }

  set pageEvents(value: Page) {
    this._pageEvents = value;
  }

  ngOnInit() {
    // this.getLocations(0, 6);
    this.getEvents(0, 6);
  }

  private pageChanged(event: PageEvent) {
    /*this._pageLocations.size = event.pageSize;
    this._pageLocations.number = event.pageIndex;

    this.getLocations(this._pageLocations.size, this._pageLocations.number);*/

    this._pageEvents.size = event.pageSize;
    this._pageEvents.number = event.pageIndex;

    this.getEvents(this._pageEvents.number, this._pageEvents.size);
  }

  private getLocations(page: number, size: number) {
    this._locationApiService.getLocations(page, size).subscribe({
      next: (result: Page) => {
        this._pageLocations = result;
        this._locations = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

  private getEvents(page: number, size: number) {
    this._eventApiService.getEvents(page, size).subscribe({
      next: (result: Page) => {
        this._pageEvents = result;
        this._events = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }


}
