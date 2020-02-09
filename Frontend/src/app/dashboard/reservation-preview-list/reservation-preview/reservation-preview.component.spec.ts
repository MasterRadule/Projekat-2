import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {ReservationPreviewComponent} from './reservation-preview.component';
import {ReservationApiService} from '../../../core/reservation-api.service';
import {Router} from '@angular/router';
import {Reservation} from '../../../shared/model/reservation.model';
import {Ticket} from '../../../shared/model/ticket.model';
import {Location as UrlLocation} from '@angular/common';
import {
  MatButtonModule,
  MatCardModule,
  MatIconModule, MatSnackBar,
  MatSnackBarModule,
} from '@angular/material';
import {CoreModule} from '../../../core/core.module';

import {By} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {ReservationComponent} from '../../../reservation/reservation.component';
import {ReservationModule} from '../../../reservation/reservation.module';
import {of} from 'rxjs';

describe('ReservationPreviewComponent', () => {
  let component: ReservationPreviewComponent;
  let fixture: ComponentFixture<ReservationPreviewComponent>;
  let reservationApiService: jasmine.SpyObj<ReservationApiService>;
  let matSnackBar: jasmine.SpyObj<MatSnackBar>;
  let router: Router;
  let urlLocation: UrlLocation;
  const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);
  const reservationNotPaid: Reservation = new Reservation(1, null, 'eventName', 1, [ticket]);
  const reservationPaid: Reservation = new Reservation(1, 'orderId', 'eventName', 1, [ticket]);


  beforeEach(() => {
    const reservationApiServiceSpy = jasmine.createSpyObj('ReservationApiService', ['cancelReservation']);
    const matSnackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    window.paypal = {
      Button: {
        render: jasmine.createSpy()
      }
    };

    TestBed.configureTestingModule({
      imports: [
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule,
        CoreModule,
        BrowserAnimationsModule,
        CoreModule,
        ReservationModule,
        RouterTestingModule.withRoutes([
          {
            path: 'dashboard/reservations/:id',
            component: ReservationComponent,
            pathMatch: 'full'
          }
        ])],
      declarations: [ReservationPreviewComponent],
      providers: [
        {provide: MatSnackBar, useValue: matSnackBarSpy},
        {provide: ReservationApiService, useValue: reservationApiServiceSpy}
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ReservationPreviewComponent);
    component = fixture.componentInstance;
    reservationApiService = TestBed.get(ReservationApiService);
    matSnackBar = TestBed.get(MatSnackBar);
    urlLocation = TestBed.get(UrlLocation);
    router = TestBed.get(Router);

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create correctly when reservation is not paid', () => {
    component.reservation = reservationNotPaid;
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#detailsButton')))
      .toBeTruthy();
    expect(fixture.debugElement.query(By.css('#cancelButton')))
      .toBeTruthy();
    expect(fixture.debugElement.query(By.css(`#pay-button-${reservationNotPaid.id}`)))
      .toBeTruthy();
    expect(fixture.debugElement.query(By.css('mat-card-title')).nativeElement.innerText)
      .toEqual(reservationNotPaid.eventName);
    expect(window.paypal.Button.render).toHaveBeenCalled();
  });

  it('should create correctly when reservation is paid', () => {
    component.reservation = reservationPaid;
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('#detailsButton')))
      .toBeTruthy();
    expect(fixture.debugElement.query(By.css('#cancelButton')))
      .toBeFalsy();
    expect(fixture.debugElement.query(By.css(`#pay-button-${reservationNotPaid.id}`)))
      .toBeFalsy();
    expect(fixture.debugElement.query(By.css('mat-card-title')).nativeElement.innerText)
      .toEqual(reservationPaid.eventName);
  });

  it('should go to reservation details page', fakeAsync(() => {
    component.reservation = reservationPaid;
    fixture.detectChanges();
    const detailsButton = fixture.debugElement.query(By.css('#detailsButton'));

    detailsButton.triggerEventHandler('click', {});
    tick();
    fixture.detectChanges();

    expect(urlLocation.path()).toBe(`/dashboard/reservations/${reservationPaid.id}`);
  }));

  it('should cancel the reservation', fakeAsync(() => {
    component.reservation = reservationNotPaid;
    fixture.detectChanges();
    reservationApiService.cancelReservation.and.returnValue(of(reservationNotPaid));

    const cancelButton = fixture.debugElement.query(By.css(`#cancelButton`));

    cancelButton.triggerEventHandler('click', {});
    tick();
    fixture.detectChanges();

    expect(reservationApiService.cancelReservation.calls.count()).toBe(1, 'oneCall');
  }));

});
