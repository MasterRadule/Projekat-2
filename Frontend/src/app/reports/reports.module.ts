import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReportsComponent} from './reports.component';
import {ChartsModule} from 'ng2-charts';
import {FlexModule} from '@angular/flex-layout';
import {CoreModule} from '../core/core.module';


@NgModule({
  declarations: [ReportsComponent],
  imports: [
    CommonModule,
    ChartsModule,
    FlexModule,
    CoreModule
  ],
  exports: [ReportsComponent]
})
export class ReportsModule {
}
