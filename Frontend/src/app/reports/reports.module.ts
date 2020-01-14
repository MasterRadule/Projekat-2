import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReportsComponent} from './reports.component';
import {NgxChartsModule} from '@swimlane/ngx-charts';


@NgModule({
  declarations: [ReportsComponent],
  imports: [
    CommonModule,
    NgxChartsModule
  ],
  exports: [ReportsComponent]
})
export class ReportsModule {
}
