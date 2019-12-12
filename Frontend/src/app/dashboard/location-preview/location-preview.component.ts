import {Component, OnInit} from '@angular/core';
import {Location} from '../../shared/model/location.model';

@Component({
  selector: 'app-location-preview',
  templateUrl: './location-preview.component.html',
  styleUrls: ['./location-preview.component.css']
})
export class LocationPreviewComponent implements OnInit {
  private location: Location;

  constructor() {
  }

  ngOnInit() {
  }

}
