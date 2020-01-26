import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ReservationComponent} from './reservation.component';
import {SharedModule} from '../shared/shared.module';
import {QRCodeModule} from 'angularx-qrcode';
import {MatButtonModule} from '@angular/material';
import {NgxPrintModule} from 'ngx-print';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {FlexModule} from '@angular/flex-layout';



@NgModule({
  declarations: [ReservationComponent],
  imports: [
    CommonModule,
    SharedModule,
    QRCodeModule,
    MatButtonModule,
    NgxPrintModule,
    ToolbarModule,
    FlexModule
  ]
})
export class ReservationModule { }
