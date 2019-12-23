import {Component, OnInit} from '@angular/core';
import {Page} from '../shared/model/page.model';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {Location} from '../shared/model/location.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private _locations: Location[];

  constructor(private _locationApiService: LocationApiService, private _snackBar: MatSnackBar) {
  }


  get locations(): Location[] {
    return this._locations;
  }

  set locations(value: Location[]) {
    this._locations = value;
  }

  ngOnInit() {
    this._locationApiService.getLocations(0, 5).subscribe({
      next: (result: Page) => {
        this._locations = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

}
