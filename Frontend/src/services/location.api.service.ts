import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {SeatGroup} from '../model/seat-group';

@Injectable({
  providedIn: 'root'
})
export class LocationApiService {
  private readonly baseUrl: string;

  constructor(private httpClient: HttpClient) {
    this.baseUrl = environment.baseUrl;
  }

  getLocations(page: number, size: number) {
    return this.httpClient.get(`${this.baseUrl}/locations?page=${page}&size=${size}`);
  }

  getLocation(id: number) {
    return this.httpClient.get(`${this.baseUrl}/locations/${id}`);
  }

  searchLocations(name: string, page: number, size: number) {
    return this.httpClient.get(`${this.baseUrl}/locations?page=${page}&size=${size}&name=${name}`);
  }

  createLocation(location: Location) {
    return this.httpClient.post<Location>(`${this.baseUrl}/locations`, location);
  }

  editLocation(location: Location) {
    return this.httpClient.put<Location>(`${this.baseUrl}/locations`, location);
  }

  getSeatGroups(locationId: number, page: number, size: number) {
    return this.httpClient.get(`${this.baseUrl}/locations/${locationId}/seat-groups?page=${page}&size=${size}`);
  }

  getSeatGroup(locationId: number, seatGroupId: number) {
    return this.httpClient.get(`${this.baseUrl}/locations/${locationId}/seat-groups/${seatGroupId}`);
  }

  createSeatGroup(locationId: number, seatGroup: SeatGroup) {
    return this.httpClient.post<SeatGroup>(`${this.baseUrl}/locations/${locationId}/seat-groups`, seatGroup);
  }
}
