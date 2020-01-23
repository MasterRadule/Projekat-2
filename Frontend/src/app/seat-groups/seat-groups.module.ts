import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SeatGroupsComponent } from './seat-groups.component';


@NgModule({
  declarations: [SeatGroupsComponent],
  imports: [
    CommonModule,
  ],
  exports: [SeatGroupsComponent]
})
export class SeatGroupsModule { }
