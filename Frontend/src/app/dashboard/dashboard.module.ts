import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule}   from '@angular/forms';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {DashboardComponent} from './dashboard.component';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatGridListModule,
  MatIconModule, MatInputModule, MatSlideToggleModule,
  MatSnackBarModule, MatToolbarModule,
  MatTooltipModule,
  MatPaginatorModule, MatSelectModule,
  MatDatepickerModule, MatNativeDateModule,
  MatFormFieldModule, MAT_DATE_FORMATS, MAT_DATE_LOCALE,
  DateAdapter
} from '@angular/material';
import {CoreModule} from '../core/core.module';
import { LocationPreviewComponent } from './location-preview-list/location-preview/location-preview.component';
import {FlexModule} from '@angular/flex-layout';
import { EventPreviewComponent } from './event-preview-list/event-preview/event-preview.component';
import { EventPreviewListComponent} from './event-preview-list/event-preview-list.component';
import { PaginatorComponent } from './paginator/paginator.component';
import { LocationPreviewListComponent } from './location-preview-list/location-preview-list.component';


@NgModule({
  declarations: [DashboardComponent, LocationPreviewComponent, EventPreviewComponent,
        EventPreviewListComponent, PaginatorComponent, LocationPreviewListComponent],
  imports: [
    CommonModule,
    FormsModule,
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
    MatPaginatorModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule
  ],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'DD.MM.YYYY.',
        },
        display: {
          dateInput: 'DD.MM.YYYY.',
          monthYearLabel: 'YYYY',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'YYYY',
        },
      },
    },
  ],
})
export class DashboardModule {
}
