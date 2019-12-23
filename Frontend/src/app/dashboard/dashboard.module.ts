import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DashboardComponent} from './dashboard.component';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatGridListModule,
  MatIconModule, MatInputModule, MatSlideToggleModule,
  MatSnackBarModule, MatToolbarModule,
  MatTooltipModule
} from '@angular/material';
import {CoreModule} from '../core/core.module';
import { LocationPreviewComponent } from './location-preview/location-preview.component';
import {FlexModule} from '@angular/flex-layout';


@NgModule({
  declarations: [DashboardComponent, LocationPreviewComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    CoreModule,
    MatGridListModule,
    MatTooltipModule,
    MatButtonToggleModule,
    MatSlideToggleModule,
    MatToolbarModule,
    MatInputModule,
    FlexModule,
  ]
})
export class DashboardModule {
}
