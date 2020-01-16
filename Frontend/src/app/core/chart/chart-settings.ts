import {ChartDataSets, ChartOptions} from 'chart.js';
import {Label} from 'ng2-charts';

export class ChartSettings {
  private _lineChartData: ChartDataSets[];
  private _lineChartLabels: Label[];
  private _lineChartOptions: (ChartOptions & { annotation: any }) = {
    responsive: true,
    scales: {
      // We use this empty structure as a placeholder for dynamic theming.
      xAxes: [{}],
      yAxes: [
        {
          id: 'y-axis-0',
          position: 'left',
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


  get lineChartData(): ChartDataSets[] {
    return this._lineChartData;
  }

  set lineChartData(value: ChartDataSets[]) {
    this._lineChartData = value;
  }

  get lineChartLabels(): Label[] {
    return this._lineChartLabels;
  }

  set lineChartLabels(value: Label[]) {
    this._lineChartLabels = value;
  }

  get lineChartOptions(): ChartOptions & { annotation: any } {
    return this._lineChartOptions;
  }

  set lineChartOptions(value: ChartOptions & { annotation: any }) {
    this._lineChartOptions = value;
  }
}
