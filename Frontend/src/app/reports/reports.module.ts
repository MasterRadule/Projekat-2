import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ReportsComponent} from './reports.component';
import {ChartsModule} from 'ng2-charts';
import {FlexModule} from '@angular/flex-layout';
import {CoreModule} from '../core/core.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {FormsModule} from '@angular/forms';
import {MatButtonModule, MatDatepickerModule, MatFormFieldModule, MatInputModule, MatSelectModule} from '@angular/material';


@NgModule({
  declarations: [ReportsComponent],
  imports: [
    CommonModule,
    ChartsModule,
    FlexModule,
    CoreModule,
    ToolbarModule,
    FormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatDatepickerModule,
    MatInputModule,
    MatSelectModule
  ],
  exports: [ReportsComponent]
})
export class ReportsModule {
}
