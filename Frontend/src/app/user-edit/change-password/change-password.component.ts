import { Component, OnInit } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Router} from '@angular/router';
import {UserEditApiService} from '../../core/user-edit-api.service';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ChangePasswordDTO} from '../../shared/model/change-password-dto.model';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  constructor(private snackBar: MatSnackBar,
              private router: Router,
              private userService: UserEditApiService) { }

  ngForm = new FormGroup({
    oldPassword: new FormControl('', [Validators.required]),
    password: new FormControl('', Validators.compose([
      Validators.required,
      Validators.pattern('(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!_*#$%^&+=])(?=\\S+$).{8,}')])),
    repeatedPassword: new FormControl('', Validators.compose([
      Validators.required,
      Validators.pattern('(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!_*#$%^&+=])(?=\\S+$).{8,}')]))
  });

  ngOnInit() {
  }

  onSubmit() {
    const changePasswordObserver = {
      next: x => {
        this.snackBar.open('Password changed successfully!', 'Dismiss', {
          duration: 3000
        });
        this.router.navigate(['/myProfile']);
      },
      error: (err: any) => {
        this.snackBar.open(JSON.parse(JSON.stringify(err)).error, 'Dismiss', {
          duration: 3000
        });
      }
    };
    const changePasswordDTO: ChangePasswordDTO = new ChangePasswordDTO(this.ngForm.controls.oldPassword.value,
      this.ngForm.controls.password.value, this.ngForm.controls.repeatedPassword.value);

    this.userService.changePassword(changePasswordDTO).subscribe(changePasswordObserver);
  }

}
