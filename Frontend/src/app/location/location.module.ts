import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SeatGroupsComponent} from './seat-groups/seat-groups.component';
import {LocationComponent} from './location.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule, MatCheckboxModule, MatInputModule} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {CoreModule} from '../core/core.module';


@NgModule({
  declarations: [LocationComponent, SeatGroupsComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    FormsModule,
    CoreModule,
    MatButtonModule
  ],
  exports: [
    LocationComponent
  ]
})
export class LocationModule {
}
