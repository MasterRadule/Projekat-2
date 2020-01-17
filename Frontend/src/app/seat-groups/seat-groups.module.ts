import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatGroupsComponent } from './seat-groups.component';
import {KonvaModule} from 'ng2-konva';



@NgModule({
  declarations: [SeatGroupsComponent],
  imports: [
    CommonModule,
    KonvaModule
  ],
  exports: [SeatGroupsComponent]
})
export class SeatGroupsModule { }
