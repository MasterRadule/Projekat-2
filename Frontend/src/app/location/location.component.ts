import {Component, OnInit, ViewChild} from '@angular/core';
import {Location} from '../shared/model/location.model';
import {ActivatedRoute, Router} from '@angular/router';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {Location as URLLocation} from '@angular/common';
import {SeatGroup} from '../shared/model/seat-group.model';
import {Page} from '../shared/model/page.model';
import {MapComponent} from '../core/map/map.component';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {
  private location: Location = new Location(undefined, '', 45.0, 45.0, false);
  private seatGroups: SeatGroup[] = [];
  private initialized = false;

  private parterre = new FormControl(false);
  private seatGroupName = new FormControl('');
  private colsNum = new FormControl(null);
  private rowsNum = new FormControl(null);
  private totalSeats = new FormControl(null);

  constructor(private route: ActivatedRoute, private locationApiService: LocationApiService, private snackBar: MatSnackBar,
              private router: Router) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.getLocation(params.id);
        this.getSeatGroups(params.id);
      } else {
        this.initialized = true;
      }
    });
  }

  getLocation(id: number) {
    this.locationApiService.getLocation(id).subscribe(
      {
        next: (result: Location) => {
          this.location = result;
          this.initialized = true;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  getSeatGroups(id: number) {
    this.locationApiService.getSeatGroups(id, 0, Number.MAX_SAFE_INTEGER).subscribe(
      {
        next: (result: Page) => {
          this.seatGroups = result.content;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  createOrEditLocation() {
    if (this.location.id) {
      this.locationApiService.editLocation(this.location).subscribe(
        {
          next: (result: Location) => {
            this.location = result;
            this.snackBar.open('Location edited successfully', 'Dismiss', {
              duration: 3000
            });
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
            this.snackBar.open('Location created successfully', 'Dismiss', {
              duration: 3000
            });
            this.router.navigate(['/dashboard/locations/', result.id]).then(r => {
            });
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
          }
        }
      );
    }
  }

  private updatePosition(position) {
    this.location.latitude = position.lat;
    this.location.longitude = position.lng;
  }

  private makeArray(n: number) {
    return Array(n);
  }

  private toggleParterre() {
    if (this.parterre.value) {
      this.colsNum.reset();
      this.rowsNum.reset();
      this.totalSeats.reset();
      this.colsNum.disable();
      this.rowsNum.disable();
    } else {
      this.colsNum.enable();
      this.rowsNum.enable();
    }
  }

  private calculateTotalSeats() {
    if (this.colsNum.value && this.rowsNum.value) {
      this.totalSeats.setValue(this.colsNum.value * this.rowsNum.value);
    }
  }

  private addSeatGroup() {
    // TODO
  }

}
