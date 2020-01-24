import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {EventComponent} from './event.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule, MatInputModule, MatTooltipModule, MatSelectModule, MatDialogModule,
    MatDatepickerModule, MatIconModule} from '@angular/material';
import {FormsModule} from '@angular/forms';
import {CoreModule} from '../core/core.module';
import {FlexModule} from '@angular/flex-layout';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {RouterModule} from '@angular/router';
import {SharedModule} from '../shared/shared.module';
import {ToolbarModule} from '../toolbar/toolbar.module';
import { AxiomSchedulerModule } from 'axiom-scheduler';
import { DialogComponent } from './dialog/dialog.component';
import {NgxMaterialTimepickerModule} from 'ngx-material-timepicker';
import { NgImageSliderModule } from 'ng-image-slider';
import { MatFileUploadModule } from 'angular-material-fileupload';

@NgModule({
  declarations: [EventComponent, DialogComponent],
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
        NgxMaterialTimepickerModule,
        NgImageSliderModule,
        MatIconModule,
        MatFileUploadModule
    ],
  exports: [
    EventComponent
  ]
})
export class EventModule {
}