import {Component, OnInit} from '@angular/core';
import {Location} from '../shared/model/location.model';
import {ActivatedRoute} from '@angular/router';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {
  private location: Location;

  constructor(private route: ActivatedRoute, private locationApiService: LocationApiService, private snackBar: MatSnackBar) {

  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.locationApiService.getLocation(params.id).subscribe(
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
    });
  }

}
