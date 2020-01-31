import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';
import {LocationSeatGroupDTO} from '../shared/model/location-seat-group-dto.model';
import {Event} from '../shared/model/event.model';

@Injectable({
  providedIn: 'root'
})
export class EventApiService {
  constructor(private _http: HttpClient) {
  }

  getEvent(eventId: number) {
    return this._http.get(`events/${eventId}`);
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
    return this._http.get(`events/search?name=${parameters.name}&locationID=${locationID}\
    &category=${category}&startDate=${parameters.startDate}&endDate=${parameters.endDate}&page=${page}&size=${size}`);
  }

  createEvent(event: Event) {
    return this._http.post(`events`, event.serialize());
  }

  editEvent(event: Event) {
    return this._http.put(`events`, event);
  }

  getEventsPicturesAndVideos(id: number) {
    return this._http.get(`events/${id}/pictures-and-videos`);
  }

  deleteMediaFile(eventID: number, id: number) {
    return this._http.delete(`events/${eventID}/pictures-and-videos/${id}`);
  }

  getEventLocationAndSeatGroups(eventID: number) {
    return this._http.get(`events/${eventID}/location`);
  }

  setEventLocationAndSeatGroups(locationSeatGroupDTO: LocationSeatGroupDTO) {
    return this._http.put(`events/location`, locationSeatGroupDTO);
  }
}
