import { Component, OnInit } from '@angular/core';
import {AuthenticationApiService} from "../../../core/authentication-api.service";
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserDTO} from "../../../shared/model/user-dto.model";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  constructor(private authService:AuthenticationApiService, private snackBar: MatSnackBar,
              private router: Router) {
  }

  ngOnInit() {
  }

  ngForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required,
      Validators.pattern("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")]),
    password: new FormControl('', Validators.compose([
      Validators.minLength(8),
      Validators.required,
      Validators.pattern("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@!_*#$%^&+=])(?=\\S+$).{8,}")]))
  });

  onSubmit() {
    const registerObserver = {
      next: x =>{
        this.snackBar.open("Registration successful! Check mail to verify account!", 'Dismiss', {
          duration: 3000
        });
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.log(err);
        this.snackBar.open(JSON.parse(JSON.stringify(err))["error"], 'Dismiss', {
          duration: 3000
        });
      }
    };
    const userDTO: UserDTO = new UserDTO(
      this.ngForm.controls['firstName'].value, this.ngForm.controls['lastName'].value,
      this.ngForm.controls['email'].value, this.ngForm.controls['password'].value
    );

    this.authService.register(userDTO).subscribe(registerObserver);
  }

}
