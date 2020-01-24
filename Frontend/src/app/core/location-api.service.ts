import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SeatGroup} from '../shared/model/seat-group.model';
import {Location} from '../shared/model/location.model';

@Injectable({
  providedIn: 'root'
})
export class LocationApiService {

  constructor(private _http: HttpClient) {
  }

  getLocations(page: number, size: number) {
    return this._http.get(`locations?page=${page}&size=${size}`);
  }

  getLocation(locationId: number) {
    return this._http.get(`locations/${locationId}`);
  }

  getLocationsOptions() {
    return this._http.get(`locations/options`);
  }

  searchLocations(name: string, page: number, size: number) {
    return this._http.get(`locations/search?page=${page}&size=${size}&name=${name}`);
  }

  createLocation(location: Location) {
    return this._http.post(`locations`, location.serialize());
  }

  editLocation(location: Location) {
    return this._http.put(`locations`, location);
  }

  getSeatGroups(locationId: number, page: number, size: number) {
    return this._http.get(`locations/${locationId}/seat-groups?page=${page}&size=${size}`);
  }

  getSeatGroup(locationId: number, seatGroupId: number) {
    return this._http.get(`locations/${locationId}/seat-groups/${seatGroupId}`);
  }

  createSeatGroup(locationId: number, seatGroup: SeatGroup) {
    return this._http.post(`locations/${locationId}/seat-groups`, seatGroup);
  }
}
