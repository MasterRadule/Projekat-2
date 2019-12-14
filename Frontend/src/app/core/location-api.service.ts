import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LocationApiService {
  private _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = environment.baseUrl;
  }

  getLocations(page: number, size: number) {
    return this._http.get(`${this._baseUrl}/locations?page=${page}&size=${size}`);
  }

  getLocation(locationId: number) {
    return this._http.get(`${this._baseUrl}/locations/${locationId}`);
  }
}
