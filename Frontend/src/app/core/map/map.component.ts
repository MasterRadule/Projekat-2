import {Component, AfterViewInit, Input} from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {
  @Input() private _latitude: number;
  @Input() private _longitude: number;
  @Input() private _zoom: number;

  private _map;
  @Input() private _mapName: string;

  constructor() {
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this._map = L.map(`${this._mapName}map`, {
      center: [this._latitude, this._longitude],
      zoom: this._zoom
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: this._zoom,
      minZoom: this._zoom
    }).addTo(this._map);

    L.marker([this._latitude, this._longitude]).addTo(this._map);
  }
}


