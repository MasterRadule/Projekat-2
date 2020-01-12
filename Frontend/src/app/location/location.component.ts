import {Component, OnInit} from '@angular/core';
import {Location} from '../shared/model/location.model';
import {ActivatedRoute} from '@angular/router';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {Location as URLLocation} from '@angular/common';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {
  private location: Location = new Location(undefined, '', 45.0, 45.0, false);

  constructor(private route: ActivatedRoute, private locationApiService: LocationApiService, private snackBar: MatSnackBar,
              private urlLocation: URLLocation) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.getLocation(params.id);
      }
    });
  }

  getLocation(id: number) {
    this.locationApiService.getLocation(id).subscribe(
      {
        next: (result: Location) => {
          this.location = result;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  cancelEditOrCreate() {
    this.urlLocation.back();
  }

  createOrEditLocation() {
    if (this.location.id) {
      this.locationApiService.editLocation(this.location).subscribe(
        {
          next: (result: Location) => {
            this.location = result;
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
          }
        }
      );
    } else {
      this.locationApiService.createLocation(this.location).subscribe(
        {
          next: (result: Location) => {
            this.location = result;
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
            console.log(message);
          }
        }
      );
    }
  }

}
