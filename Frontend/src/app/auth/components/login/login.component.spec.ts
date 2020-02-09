import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import { LoginComponent } from './login.component';
import {AuthenticationApiService} from '../../../core/authentication-api.service';
import {CommonModule, Location as UrlLocation} from '@angular/common';
import {Router} from '@angular/router';
import {ToolbarModule} from '../../../toolbar/toolbar.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {RouterTestingModule} from '@angular/router/testing';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Overlay} from '@angular/cdk/overlay';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {EventPreviewListComponent} from '../../../dashboard/event-preview-list/event-preview-list.component';
import {DashboardModule} from '../../../dashboard/dashboard.module';
import {of} from 'rxjs';


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authenticationApiServiceSpy: jasmine.SpyObj<AuthenticationApiService>;
  let router: Router;
  let urlLocation: UrlLocation;

  function updateForm(userEmail, userPassword) {
    fixture.componentInstance.ngForm.controls.email.setValue(userEmail);
    fixture.componentInstance.ngForm.controls.password.setValue(userPassword);
  }

  beforeEach(async(() => {
    const authSpy = jasmine.createSpyObj('AuthenticationApiService', ['login', 'getRole']);
    TestBed.configureTestingModule({
      imports: [
        CommonModule,
        ToolbarModule,
        MatCardModule,
        MatInputModule,
        MatButtonModule,
        ReactiveFormsModule,
        FormsModule,
        DashboardModule,
        BrowserAnimationsModule,
        RouterTestingModule.withRoutes([
          {
            path: 'dashboard/events/preview',
            component: EventPreviewListComponent,
            pathMatch: 'full'
          }
        ])
      ],
      declarations: [ LoginComponent],
      providers: [
        MatSnackBar,
        Overlay,
        {provide: AuthenticationApiService, useValue: authSpy}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    authenticationApiServiceSpy = TestBed.get(AuthenticationApiService);
    authenticationApiServiceSpy.getRole.and.returnValue('NO_ROLE');
    authenticationApiServiceSpy.login.and.returnValue(of('$d5a54d6a41d2sa5d4af4.da6fa5sf5a64f.dasdas'));
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    urlLocation = TestBed.get(UrlLocation);
    router = TestBed.get(Router);
    router.navigate(['/login']);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('display email error message when email is blank', () => {
    updateForm('', '123');
    const emailInput = fixture.debugElement.nativeElement.querySelector('#email');
    emailInput.focus();
    emailInput.click();
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#loginButton');
    expect(button.disabled).toBeTruthy();

    const emailErrorMsg = fixture.debugElement.nativeElement.querySelector('#errorEmail');
    expect(emailErrorMsg).toBeDefined();
    expect(emailErrorMsg.innerHTML).toContain('Not a valid email');
    expect(emailInput.classList).toContain('ng-invalid');
  });

  it('when password is blank, password field should display red outline', () => {
    updateForm('a@gmail.com', '');
    const passwordInput = fixture.debugElement.nativeElement.querySelector('#password');
    passwordInput.focus();
    fixture.detectChanges();

    const button = fixture.debugElement.nativeElement.querySelector('#loginButton');
    expect(button.disabled).toBeTruthy();

    expect(passwordInput.classList).toContain('ng-invalid');
  });

  it('loginService login() should called', () => {
    updateForm('a@gmail.com', '123');
    fixture.detectChanges();
    const button = fixture.debugElement.nativeElement.querySelector('#loginButton');
    expect(button.disabled).toBeFalsy();
    button.click();
    fixture.detectChanges();
    expect(authenticationApiServiceSpy.login).toHaveBeenCalled();
  });

  it('should login', fakeAsync(() => {
    updateForm('AliciaBabcock952@nowhere.com', '123');
    fixture.detectChanges();
    const button = fixture.debugElement.nativeElement.querySelector('#loginButton');
    expect(button.disabled).toBeFalsy();
    button.click();
    fixture.detectChanges();
    tick(1000);
    expect(urlLocation.path()).toBe(`/dashboard/events/preview`);
  }));

});
