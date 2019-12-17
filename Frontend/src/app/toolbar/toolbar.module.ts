import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToolbarComponent } from './toolbar.component';
import {MatToolbarModule} from '@angular/material';
import {MatButtonModule} from '@angular/material/button';


@NgModule({
  declarations: [ToolbarComponent],
  exports: [
    ToolbarComponent
  ],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule
  ]
})
export class ToolbarModule { }
