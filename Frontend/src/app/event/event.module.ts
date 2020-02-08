import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EventComponent} from './event.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {
  MatButtonModule, MatInputModule, MatTooltipModule, MatSelectModule, MatDialogModule,
  MatDatepickerModule, MatIconModule, MatTableModule, MatMenuModule
} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {CoreModule} from '../core/core.module';
import {FlexModule} from '@angular/flex-layout';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {AxiomSchedulerModule} from 'axiom-scheduler';
import {DialogComponent} from './dialog/dialog.component';
import {NgImageSliderModule} from 'ng-image-slider';
import {FileSelectDirective, FileDropDirective} from 'ng2-file-upload';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {SeatGroupsModule} from '../seat-groups/seat-groups.module';
import {TicketsTableComponent} from './tickets-table/tickets-table.component';

@NgModule({
  declarations: [EventComponent, DialogComponent, FileDropDirective, FileSelectDirective, TicketsTableComponent],
  entryComponents: [DialogComponent],
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
    MatSelectModule,
    AxiomSchedulerModule,
    MatDialogModule,
    MatDatepickerModule,
    NgImageSliderModule,
    MatIconModule,
    MatProgressBarModule,
    SeatGroupsModule,
    MatTableModule,
    MatMenuModule,
    SharedModule
  ],
  exports: [
    EventComponent
  ]
})
export class EventModule {
}
