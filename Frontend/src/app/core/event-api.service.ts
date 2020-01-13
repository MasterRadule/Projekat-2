import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';

@Injectable({
  providedIn: 'root'
})
export class EventApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = environment.baseUrl;
  }

  getEvents(page: number, size: number) {
    return this._http.get(`${this._baseUrl}/events?page=${page}&size=${size}`);
  }

  searchEvents(parameters: SearchEventsDTO, page: number, size: number) {
  	let locationID: number|string = parameters.locationID == null ? "": parameters.locationID;
  	let category: string = parameters.category == null ? "" : parameters.category;
  	return this._http.get(`${this._baseUrl}/events/search?name=${parameters.name}&locationID=${locationID}&category=${category}&startDate=${parameters.startDate}&endDate=${parameters.endDate}&page=${page}&size=${size}`);
  }
}
