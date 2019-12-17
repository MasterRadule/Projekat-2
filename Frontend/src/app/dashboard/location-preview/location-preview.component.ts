import {Component, Input, OnInit} from '@angular/core';
import {Location} from '../../shared/model/location.model';
import {MatSlideToggleChange} from '@angular/material';
import {LocationApiService} from '../../core/location-api.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-location-preview',
  templateUrl: './location-preview.component.html',
  styleUrls: ['./location-preview.component.scss']
})
export class LocationPreviewComponent implements OnInit {
  @Input() private location: Location;

  constructor(private locationApiService: LocationApiService, private snackBar: MatSnackBar) {
  }

  ngOnInit() {
  }

  private toggleLocationStatus($event: MatSlideToggleChange) {
    this.location.disabled = !$event.checked;
    const state = this.location.disabled ? 'disabled' : 'enabled';

    this.locationApiService.editLocation(this.location).subscribe(
      {
        next: (result: Location) => {
          this.location = result;
          this.snackBar.open(`Location ${state} successfully`, 'Dismiss', {
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
  }
}
