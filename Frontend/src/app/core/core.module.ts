import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map/map.component';
import {LocationApiService} from './location-api.service';
import {EventApiService} from './event-api.service';
import {ReportsApiService} from './reports-api.service';
import { ChartComponent } from './chart/chart.component';
import {ChartsModule} from 'ng2-charts';


@NgModule({
  declarations: [MapComponent, ChartComponent],
    imports: [
        CommonModule,
        ChartsModule
    ],
  exports: [
    MapComponent,
    ChartComponent
  ]
})
export class CoreModule {
}
