import {Component, OnInit} from '@angular/core';
import {ReportsApiService} from '../core/reports-api.service';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss']
})
export class ReportsComponent implements OnInit {

  constructor(private reportsApiService: ReportsApiService) {
  }

  ngOnInit() {
  }

}
