import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map/map.component';
import {LocationApiService} from './location-api.service';
import {EventApiService} from './event-api.service';
import {ReportsApiService} from './reports-api.service';


@NgModule({
  declarations: [MapComponent],
  imports: [
    CommonModule
  ],
  exports: [
    MapComponent
  ],
  providers: [
    LocationApiService,
    EventApiService,
    ReportsApiService
  ]
})
export class CoreModule {
}
