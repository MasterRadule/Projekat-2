import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';
import {Event} from '../shared/model/event.model';

@Injectable({
  providedIn: 'root'
})
export class EventApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = environment.baseUrl;
  }

  getEvent(eventId: number) {
    return this._http.get(`${this._baseUrl}/events/${eventId}`);
  }

  getEvents(page: number, size: number) {
    return this._http.get(`${this._baseUrl}/events?page=${page}&size=${size}`);
  }

  getEventsOptions() {
    return this._http.get(`${this._baseUrl}/events/options`);
  }

  searchEvents(parameters: SearchEventsDTO, page: number, size: number) {
    const locationID: number | string = parameters.locationID == null ? '' : parameters.locationID;
    const category: string = parameters.category == null ? '' : parameters.category;
    return this._http.get(`${this._baseUrl}/events/search?name=${parameters.name}&locationID=${locationID}&category=${category}&startDate=${parameters.startDate}&endDate=${parameters.endDate}&page=${page}&size=${size}`);
  }

  createEvent(event: Event) {
    return new Event(null, "", "", null, false, false, 0, 0, null);
    //return this._http.post(`${this._baseUrl}/event`, location.serialize());
  }

  editEvent(event: Event) {
    return this._http.put(`${this._baseUrl}/events`, event);
  }

  getEventsPicturesAndVideos(id: number) {
    return this._http.get(`${this._baseUrl}/events/${id}/pictures-and-videos`);
  }

  deleteMediaFile(eventID: number, id: number) {
    return this._http.delete(`${this._baseUrl}/events/${eventID}/pictures-and-videos/${id}`);
  }
}
