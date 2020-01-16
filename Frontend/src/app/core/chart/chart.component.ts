import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from 'chart.js';
import {BaseChartDirective, Color, Label} from 'ng2-charts';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {
  @Input() private lineChartData: ChartDataSets[];
  @Input() private lineChartLabels: Label[];
  @Input() private lineChartOptions: ChartOptions;

  private lineChartLegend = true;
  private lineChartType: ChartType = 'line';

  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  constructor() {
  }

  ngOnInit() {
  }

}
