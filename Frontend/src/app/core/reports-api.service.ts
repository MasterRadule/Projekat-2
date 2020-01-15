import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ReportRequestDTO} from '../shared/model/report-request-dto.model';

@Injectable({
  providedIn: 'root'
})
export class ReportsApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = environment.baseUrl;
  }

  getReport(reportRequest: ReportRequestDTO) {
    Object.keys(reportRequest)
      .forEach(key => reportRequest[key] === undefined || reportRequest[key] === null ? delete reportRequest[key] : {});

    const params = new HttpParams({
      fromObject: {
        startDate: String(reportRequest.startDate),
        endDate: String(reportRequest.endDate),
        locationId: reportRequest.locationId ? String(reportRequest.locationId) : '',
        eventId: reportRequest.eventId ? String(reportRequest.eventId) : ''
      }
    });
    return this._http.get(`${this._baseUrl}/reports`, {params});
  }
}
