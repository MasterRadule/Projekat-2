import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';

@Injectable({
  providedIn: 'root'
})
export class EventApiService {
  constructor(private _http: HttpClient) {
  }

  getEvents(page: number, size: number) {
    return this._http.get(`events?page=${page}&size=${size}`);
  }

  getEventsOptions() {
    return this._http.get(`events/options`);
  }

  searchEvents(parameters: SearchEventsDTO, page: number, size: number) {
    const locationID: number | string = parameters.locationID == null ? '' : parameters.locationID;
    const category: string = parameters.category == null ? '' : parameters.category;
    return this._http.get(`events/search?name=${parameters.name}&locationID=${locationID}&category=${category}&startDate=${parameters.startDate}&endDate=${parameters.endDate}&page=${page}&size=${size}`);
  }
}
