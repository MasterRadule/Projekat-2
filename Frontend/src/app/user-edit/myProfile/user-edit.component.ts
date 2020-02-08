import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {LoginDto} from '../../shared/model/login-dto.model';
import {UserEditApiService} from '../../core/user-edit-api.service';
import {UserDTO} from '../../shared/model/user-dto.model';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit {
  private user: any;
  constructor(private snackBar: MatSnackBar,
              private router: Router,
              private userService: UserEditApiService) { }

  ngForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required,
      Validators.pattern('^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$')])
  });

  ngOnInit() {
    this.user = this.userService.getUser().subscribe(
      result => {this.user = result;
                 this.ngForm.controls.firstName.setValue(result.firstName);
                 this.ngForm.controls.lastName.setValue(result.lastName);
                 this.ngForm.controls.email.setValue(result.email); },
      error => {alert(error.error);  });
    this.ngForm.controls.email.disable();
  }

  onSubmit() {
    const editProfileObserver = {
      next: x => {
        this.snackBar.open('User edited successfully!', 'Dismiss', {
          duration: 3000
        });
        this.router.navigate(['/myProfile']);
      },
      error: (err: any) => {
        console.log(err);
        this.snackBar.open(JSON.parse(JSON.stringify(err)).error, 'Dismiss', {
          duration: 3000
        });
      }
    };
    const userDTO: UserDTO = new UserDTO(
      this.user.id, this.ngForm.controls.firstName.value, this.ngForm.controls.lastName.value, this.user.email, this.user.password
    );
    this.userService.edit(userDTO).subscribe(editProfileObserver);
  }


}
