import {Component, Input} from '@angular/core';
import {LocationApiService} from '../../../core/location-api.service';
import {MatSlideToggleChange, MatSnackBar} from '@angular/material';
import {Location} from '../../../shared/model/location.model';

@Component({
  selector: 'app-location-preview',
  templateUrl: './location-preview.component.html',
  styleUrls: ['./location-preview.component.scss']
})
export class LocationPreviewComponent {
  @Input() location: Location;

  constructor(private locationApiService: LocationApiService, private snackBar: MatSnackBar) {
  }

  private toggleLocationStatus($event: MatSlideToggleChange) {
    this.location.disabled = !$event.checked;
    const state = this.location.disabled ? 'disabled' : 'enabled';

    this.locationApiService.editLocation(this.location).subscribe(
      {
        next: (result: Location) => {
          this.location = result;
          this.snackBar.open(`Location ${state} successfully`, 'Dismiss', {
            duration: 3000,
            panelClass: ['snackbar'] // css should be added
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
