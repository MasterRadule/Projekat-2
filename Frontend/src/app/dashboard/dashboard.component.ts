import {Component, OnInit} from '@angular/core';
import {Page} from '../shared/model/page.model';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar, PageEvent} from '@angular/material';
import {Location} from '../shared/model/location.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private _locations: Location[];
  private _page: Page;

  constructor(private _locationApiService: LocationApiService, private _snackBar: MatSnackBar) {
  }


  get page(): Page {
    return this._page;
  }

  set page(value: Page) {
    this._page = value;
  }

  get locations(): Location[] {
    return this._locations;
  }

  set locations(value: Location[]) {
    this._locations = value;
  }

  ngOnInit() {
    this.getLocations(0, 6);
  }

  private pageChanged(event: PageEvent) {
    this._page.size = event.pageSize;
    this._page.number = event.pageIndex;

    this.getLocations(this._page.size, this._page.number);
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

}
