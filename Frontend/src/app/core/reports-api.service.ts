import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
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
    return this._http.get(`${this._baseUrl}/reports?startDate=${reportRequest.startDate}&endDate=${reportRequest.endDate}&locationId=${reportRequest.locationId}&eventId=${reportRequest.eventId}`);
  }
}
