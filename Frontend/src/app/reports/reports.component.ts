import {Component, OnInit} from '@angular/core';
import {ReportsApiService} from '../core/reports-api.service';
import {ReportRequestDTO} from '../shared/model/report-request-dto.model';
import {ReportDTO} from '../shared/model/report-dto.model';
import {MatSnackBar} from '@angular/material';
import {ChartSettings} from '../core/chart/chart-settings';
import {Location} from '../shared/model/location.model';
import {Event} from '../shared/model/event.model';
import {LocationApiService} from '../core/location-api.service';
import {EventApiService} from '../core/event-api.service';
import * as moment from 'moment';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  private chart1Settings: ChartSettings = new ChartSettings();
  private chart2Settings: ChartSettings = new ChartSettings();

  private startDate: Date;
  private endDate: Date;
  private _locationOptions: Location[] = [];
  private _eventOptions: Event[] = [];

  private reportRequest: ReportRequestDTO = new ReportRequestDTO(null, null, null, null);

  constructor(private reportsApiService: ReportsApiService, private snackBar: MatSnackBar,
              private locationApiService: LocationApiService, private eventApiService: EventApiService) {
  }


  ngOnInit() {
    this.getLocationsOptions();
    this.getEventsOptions();
  }


  get locationOptions(): Location[] {
    return this._locationOptions;
  }

  set locationOptions(value: Location[]) {
    this._locationOptions = value;
  }

  get eventOptions(): Event[] {
    return this._eventOptions;
  }

  set eventOptions(value: Event[]) {
    this._eventOptions = value;
  }

  private getLocationsOptions() {
    this.locationApiService.getLocationsOptions().subscribe({
      next: (result: Location[]) => {
        this._locationOptions = result;
      },
      error: (message: string) => {
        this.snackBar.open(message);
      }
    });
  }

  private getEventsOptions() {
    this.eventApiService.getEventsOptions().subscribe({
      next: (result: Event[]) => {
        this._eventOptions = result;
      },
      error: (message: string) => {
        this.snackBar.open(message);
      }
    });
  }

  private getReport(reportRequest: ReportRequestDTO) {
    this.reportsApiService.getReport(reportRequest).subscribe(
      {
        next: (result: ReportDTO) => {
          this.chart1Settings.lineChartLabels = result.labels;
          this.chart2Settings.lineChartLabels = result.labels;

          this.chart1Settings.lineChartData = [
            {
              data: result.tickets,
              label: 'Tickets'
            }
          ];

          this.chart2Settings.lineChartData = [
            {
              data: result.earnings,
              label: 'Earnings'
            }
          ];
        },
        error: (message: any) => {
          this.snackBar.open(message.error, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }


  private onSubmit() {
    if (this.startDate === undefined || this.endDate === undefined) {
      this.snackBar.open('Start and end date must be specified', 'Dismiss', {
        duration: 3000
      });
    } else {
      this.reportRequest.startDate = moment(this.startDate).valueOf();
      this.reportRequest.endDate = moment(this.endDate).valueOf();
      this.getReport(this.reportRequest);
    }
  }

}
