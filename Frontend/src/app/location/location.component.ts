import {Component, OnInit, ViewChild} from '@angular/core';
import {Location} from '../shared/model/location.model';
import {ActivatedRoute, Router} from '@angular/router';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {SeatGroup} from '../shared/model/seat-group.model';
import {Page} from '../shared/model/page.model';
import {FormControl, FormGroup} from '@angular/forms';
import {SeatGroupsComponent} from '../seat-groups/seat-groups.component';
import {AuthenticationApiService} from '../core/authentication-api.service';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {
  private location: Location = new Location(undefined, '', 45.0, 45.0, false);
  private initialized = false;
  private role: string;
  private seatComponentMode: string;

  private addSeatGroupForm = new FormGroup({
    parterre: new FormControl(false),
    seatGroupName: new FormControl(''),
    colsNum: new FormControl(null),
    rowsNum: new FormControl(null),
    totalSeats: new FormControl(null)
  });

  @ViewChild(SeatGroupsComponent, {static: false}) seatGroupComponent: SeatGroupsComponent;

  constructor(private route: ActivatedRoute, private locationApiService: LocationApiService, private snackBar: MatSnackBar,
              private router: Router, private authService: AuthenticationApiService) {
    this.role = this.authService.getRole();
    this.seatComponentMode = this.role.concat("_LOCATION");
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
    if (this.addSeatGroupForm.controls.parterre.value) {
      this.addSeatGroupForm.controls.colsNum.reset();
      this.addSeatGroupForm.controls.rowsNum.reset();
      this.addSeatGroupForm.controls.totalSeats.reset();
      this.addSeatGroupForm.controls.colsNum.disable();
      this.addSeatGroupForm.controls.rowsNum.disable();
    } else {
      this.addSeatGroupForm.controls.colsNum.enable();
      this.addSeatGroupForm.controls.rowsNum.enable();
    }
  }

  private calculateTotalSeats() {
    if (this.addSeatGroupForm.controls.colsNum.value && this.addSeatGroupForm.controls.rowsNum.value) {
      this.addSeatGroupForm.controls.totalSeats.setValue(this.addSeatGroupForm.controls.colsNum.value *
        this.addSeatGroupForm.controls.rowsNum.value);
    }
  }

  private addSeatGroup() {
    const newSeatGroup = new SeatGroup();
    newSeatGroup.xCoordinate = 0;
    newSeatGroup.yCoordinate = 0;
    newSeatGroup.id = null;
    newSeatGroup.angle = 0;
    newSeatGroup.rowsNum = this.addSeatGroupForm.controls.rowsNum.value;
    newSeatGroup.colsNum = this.addSeatGroupForm.controls.colsNum.value;
    newSeatGroup.name = this.addSeatGroupForm.controls.seatGroupName.value;
    newSeatGroup.parterre = this.addSeatGroupForm.controls.parterre.value;
    newSeatGroup.totalSeats = this.addSeatGroupForm.controls.totalSeats.value;
    newSeatGroup.changed = false;
    this.createSeatGroup(newSeatGroup);
  }

}
