import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LocationComponent} from './location.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule, MatInputModule, MatTooltipModule} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {CoreModule} from '../core/core.module';
import {FlexModule} from '@angular/flex-layout';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RouterModule} from '@angular/router';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {SeatGroupsModule} from '../seat-groups/seat-groups.module';


@NgModule({
  declarations: [LocationComponent],
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
    ToolbarModule,
    SeatGroupsModule,
  ],
  exports: [
    LocationComponent
  ]
})
export class LocationModule {
}
