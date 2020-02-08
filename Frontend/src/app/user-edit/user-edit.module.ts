import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {UserEditComponent} from './myProfile/user-edit.component';
import {ToolbarModule} from '../toolbar/toolbar.module';
import {MatCardModule} from '@angular/material/card';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIconModule} from '@angular/material/icon';
import { ChangePasswordComponent } from './change-password/change-password.component';
import {RouterModule} from '@angular/router';

@NgModule({
  declarations: [UserEditComponent, ChangePasswordComponent],
  imports: [
    CommonModule,
    ToolbarModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    ReactiveFormsModule,
    FormsModule,
    MatTooltipModule,
    MatIconModule,
    RouterModule
  ],
  exports: [UserEditComponent]
})
export class UserEditModule { }
