import {Component, OnInit} from '@angular/core';
import {ReportsApiService} from '../core/reports-api.service';
import {ReportRequestDTO} from '../shared/model/report-request-dto.model';
import {ReportDTO} from '../shared/model/report-dto.model';
import {MatSnackBar} from '@angular/material';
import {ChartDataSets, ChartOptions} from 'chart.js';
import {Label} from 'ng2-charts';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {
  public lineChartData: ChartDataSets[] = [
    {data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A'},
    {data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B'},
    {data: [180, 480, 770, 90, 1000, 270, 400], label: 'Series C', yAxisID: 'y-axis-1'}
  ];
  public lineChartLabels: Label[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
  public lineChartOptions: (ChartOptions & { annotation: any }) = {
    responsive: true,
    scales: {
      // We use this empty structure as a placeholder for dynamic theming.
      xAxes: [{}],
      yAxes: [
        {
          id: 'y-axis-0',
          position: 'left',
        },
        {
          id: 'y-axis-1',
          position: 'right',
          gridLines: {
            color: 'rgba(255,0,0,0.3)',
          },
          ticks: {
            fontColor: 'red',
          }
        }
      ]
    },
    annotation: {
      annotations: [
        {
          type: 'line',
          mode: 'vertical',
          scaleID: 'x-axis-0',
          value: 'March',
          borderColor: 'orange',
          borderWidth: 2,
          label: {
            enabled: true,
            fontColor: 'orange',
            content: 'LineAnno'
          }
        },
      ],
    },
  };

  constructor(private reportsApiService: ReportsApiService, private snackBar: MatSnackBar) {
  }


  ngOnInit() {
    this.reportsApiService.getReport(new ReportRequestDTO((new Date()).getTime(), ((new Date()).getTime() * 2))).subscribe(
      {
        next: (result: ReportDTO[]) => {
          this.transformData(result);
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  private transformData(data: ReportDTO[]) {
    const attendance = {
      name: 'Attendance',
      series: []
    };

    const earnings = {
      name: 'Earnings',
      series: []
    };

    for (const dayReport of data) {
      attendance.series.push({
        name: dayReport.date,
        value: dayReport.ticketCount
      });

      earnings.series.push({
        name: dayReport.date,
        value: dayReport.earnings
      });
    }
  }


}
