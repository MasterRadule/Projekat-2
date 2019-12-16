import {Component, OnInit} from '@angular/core';
import {LocationApiService} from '../core/location-api.service';
import {Page} from '../shared/model/page.model';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private _locations: Location[];

  constructor(private _locationApiService: LocationApiService, private _snackBar: MatSnackBar) {
  }

  ngOnInit() {
    this._locationApiService.getLocations(0, 3).subscribe({
      next: (result: Page) => {
        this._locations = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

}
