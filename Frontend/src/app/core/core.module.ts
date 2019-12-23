import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MapComponent} from './map/map.component';
import {LocationApiService} from './location-api.service';


@NgModule({
  declarations: [MapComponent],
  imports: [
    CommonModule
  ],
  exports: [
    MapComponent
  ],
  providers: [
    LocationApiService
  ]
})
export class CoreModule {
}
