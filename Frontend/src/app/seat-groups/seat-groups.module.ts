import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SeatGroupsComponent} from './seat-groups.component';
import {MatButtonModule, MatCheckboxModule, MatFormFieldModule, MatInputModule, MatSelectModule} from '@angular/material';
import {FlexModule} from '@angular/flex-layout';
import {FormsModule} from '@angular/forms';


@NgModule({
  declarations: [SeatGroupsComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    FlexModule,
    MatSelectModule,
    MatButtonModule,
    FormsModule,
  ],
  exports: [SeatGroupsComponent]
})
export class SeatGroupsModule {
}
