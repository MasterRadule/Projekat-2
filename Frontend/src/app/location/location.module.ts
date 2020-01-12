import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SeatGroupsComponent} from './seat-groups/seat-groups.component';
import {LocationComponent} from './location.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule, MatInputModule, MatTooltipModule} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {CoreModule} from '../core/core.module';
import {FlexModule} from '@angular/flex-layout';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../shared/shared.module';


@NgModule({
  declarations: [LocationComponent, SeatGroupsComponent],
  imports: [
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    FormsModule,
    CoreModule,
    MatButtonModule,
    FlexModule,
    RouterModule,
    MatTooltipModule,
  ],
  exports: [
    LocationComponent
  ]
})
export class LocationModule {
}
