import {Component, EventEmitter, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Location} from '../shared/model/location.model';
import {ActivatedRoute, Router} from '@angular/router';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {Location as URLLocation} from '@angular/common';
import {SeatGroup} from '../shared/model/seat-group.model';
import {Page} from '../shared/model/page.model';
import {MapComponent} from '../core/map/map.component';
import {FormControl} from '@angular/forms';
import {SeatGroupsComponent} from '../seat-groups/seat-groups.component';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {
  private location: Location = new Location(undefined, '', 45.0, 45.0, false);
  private initialized = false;

  private parterre = new FormControl(false);
  private seatGroupName = new FormControl('');
  private colsNum = new FormControl(null);
  private rowsNum = new FormControl(null);
  private totalSeats = new FormControl(null);

  @ViewChild(SeatGroupsComponent, {static: false}) seatGroupComponent: SeatGroupsComponent;

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

  private getLocation(id: number) {
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

  private getSeatGroups(id: number) {
    this.locationApiService.getSeatGroups(id, 0, Number.MAX_SAFE_INTEGER).subscribe(
      {
        next: (result: Page) => {
          this.seatGroupComponent.seatGroups = result.content;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  private createLocation() {
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

  private editLocation() {
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
      });
  }

  private updateSeatGroups() {
    this.seatGroupComponent.seatGroups.forEach((seatGroup, index, list) => {
      if (seatGroup.changed) {
        this.locationApiService.editSeatGroupPosition(this.location.id, seatGroup).subscribe({
          next: (result: SeatGroup) => {
            result.changed = false;
            list[index] = result;
          }
        });
      }
    });
  }

  private createOrEditLocation() {
    if (this.location.id) {
      this.updateSeatGroups();
      this.editLocation();
    } else {
      this.createLocation();
    }
  }

  private createSeatGroup(seatGroup: SeatGroup) {
    this.locationApiService.createSeatGroup(this.location.id, seatGroup).subscribe({
        next: (result: SeatGroup) => {
          this.snackBar.open('Seat group created successfully', 'Dismiss', {
            duration: 3000
          });
          this.seatGroupComponent.addSeatGroup(result);
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
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
    const newSeatGroup = new SeatGroup();
    newSeatGroup.xCoordinate = 0;
    newSeatGroup.yCoordinate = 0;
    newSeatGroup.id = null;
    newSeatGroup.angle = 0;
    newSeatGroup.rowsNum = this.rowsNum.value;
    newSeatGroup.colsNum = this.colsNum.value;
    newSeatGroup.name = this.seatGroupName.value;
    newSeatGroup.parterre = this.parterre.value;
    newSeatGroup.totalSeats = this.totalSeats.value;
    newSeatGroup.changed = false;
    this.createSeatGroup(newSeatGroup);
  }

}
