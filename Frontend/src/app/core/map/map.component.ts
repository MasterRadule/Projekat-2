import {AfterViewInit, Component, Input} from '@angular/core';
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
  @Input() private _maxZoom: number;

  private _map;
  @Input() private _mapName: string;

  constructor() {
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this._map = L.map(`${this._mapName}map`).setView([this._latitude, this._longitude], this._zoom);

    L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
      maxZoom: this._maxZoom,
      id: 'mapbox/streets-v11',
      accessToken: 'pk.eyJ1IjoiZHJhZ2FuOTciLCJhIjoiY2s0OHdkbnN6MDQ1azNubW1qYXN3MWhnOSJ9.IorNULTY9svXvs1aVmNesg'
    }).addTo(this._map);

    L.marker([this._latitude, this._longitude],
      {
        draggable: false
      }).addTo(this._map);
  }

}
