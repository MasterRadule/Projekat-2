import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LocationApiService} from './location-api.service';
import { MapComponent } from './map/map.component';


@NgModule({
  declarations: [MapComponent],
  imports: [
    CommonModule,
  ],
  exports: [
    MapComponent
  ],
  providers: [LocationApiService]
})
export class CoreModule {
}
