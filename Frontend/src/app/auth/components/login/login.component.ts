import { Input, Component, Output, EventEmitter, OnInit } from '@angular/core';
import {FormControl, FormGroup, NgForm} from "@angular/forms";
import {AuthenticationApiService} from "../../../core/authentication-api.service";
import {LoginDto} from "../../../shared/model/login-dto.model";
import {Observable} from "rxjs";
import {MatSnackBar} from "@angular/material";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  constructor(private authService:AuthenticationApiService, private snackBar: MatSnackBar,
              private router: Router) {
  }

  ngOnInit() {
  }
  token:string = null;
  ngForm = new FormGroup({
    email: new FormControl(''),
    password: new FormControl(''),
  });

  onSubmit() {
    const loginObserver = {
      next: x =>{
        console.log(x);
        localStorage.setItem('token', x);
        this.snackBar.open("Welcome!", 'Dismiss', {
          duration: 3000
        });
        this.router.navigate(['/dashboard/events/preview']);
      },
      error: (err: any) => {
        console.log(err);
        this.snackBar.open(JSON.parse(JSON.stringify(err))["error"], 'Dismiss', {
          duration: 3000
        });
      }
    };
    const loginDTO: LoginDto = new LoginDto(
      this.ngForm.controls['email'].value, this.ngForm.controls['password'].value
    );

    this.authService.login(loginDTO).subscribe(loginObserver);

  }

  @Input() error: string | null;

}
