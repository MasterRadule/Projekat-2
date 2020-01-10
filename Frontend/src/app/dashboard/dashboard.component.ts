import {Component, Input, OnInit} from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import {Page} from '../shared/model/page.model';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar, PageEvent} from '@angular/material';
import {Location} from '../shared/model/location.model';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';
import {EventApiService} from '../core/event-api.service';
import * as moment from 'moment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private _locations: Location[];
  private _events: Event[];
  private _page: Page;
  private _content: string;
  private _eventCategories: string[];
  private _locationsOptions: Location[];
  private _searchParameters: SearchEventsDTO;

  constructor(private _locationApiService: LocationApiService, private _eventApiService: EventApiService,
              private _snackBar: MatSnackBar, private route: ActivatedRoute) {
    this._eventCategories = ["Music", "Sport", "Fair", "Movie", "Performance", "Competition"];
    this.getLocationsOptions();
    this._searchParameters = new SearchEventsDTO("", null, null, "", "");
  }

  get content(): string {
    return this._content;
  }

  set content(value: string) {
    this._content = value;
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

  get page(): Page {
    return this._page;
  }

  set page(value: Page) {
    this._page = value;
  }

  get eventCategories(): string[] {
    return this._eventCategories;
  }

  set eventCategories(value: string[]) {
    this._eventCategories = value;
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this._content = params.get('content');
      switch (this._content) {
        case 'events':
          this.getEvents(0, 6);
          break;
        case 'locations':
          this.getLocations(0, 6);
          break;
      }
    });
  }

  private pageChanged(event: PageEvent) {
    this._page.size = event.pageSize;
    this._page.number = event.pageIndex;
    if (this._content === 'locations') {
      this.getLocations(this._page.number, this._page.size);
    } else {
      this.getEvents(this._page.number, this._page.size);
    }
  }

  private getLocationsOptions() {
    this._locationApiService.getLocationsOptions().subscribe({
      next: (result: Location[]) => {
        this._locationsOptions = result;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

  private getLocations(page: number, size: number) {
    this._locationApiService.getLocations(page, size).subscribe({
      next: (result: Page) => {
        this._page = result;
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
        this._page = result;
        this._events = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

  private onSubmit() {
    let parameters: SearchEventsDTO = new SearchEventsDTO(this._searchParameters.name,
      this._searchParameters.locationID, this._searchParameters.category, 
      this._searchParameters.fromDate, this._searchParameters.endDate);
    if (parameters.fromDate !== "") {
      parameters.fromDate = moment(parameters.fromDate).format("DD.MM.YYYY. HH:mm");
    }
    if (parameters.endDate !== "") {
      parameters.endDate = moment(parameters.endDate).format("DD.MM.YYYY. HH:mm");
    }

    this._eventApiService.searchEvents(parameters, 0, 6).subscribe({
      next: (result: Page) => {
        this._page = result;
        this._events = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }
}
