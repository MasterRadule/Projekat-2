import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {LocationPreviewComponent} from './location-preview/location-preview.component';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBarModule} from '@angular/material';
import {CoreModule} from '../core/core.module';
import {MatGridListModule} from '@angular/material/grid-list';


@NgModule({
  declarations: [DashboardComponent, LocationPreviewComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    CoreModule,
    MatGridListModule
  ]
})
export class DashboardModule {
}
