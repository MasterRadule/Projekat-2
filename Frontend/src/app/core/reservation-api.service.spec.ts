import {TestBed} from '@angular/core/testing';

import {ReservationApiService} from './reservation-api.service';
import {HttpClient} from '@angular/common/http';
import {Page} from '../shared/model/page.model';
import {of} from 'rxjs';
import {Ticket} from '../shared/model/ticket.model';
import {Reservation} from '../shared/model/reservation.model';
import {Location} from '../shared/model/location.model';
import {PaymentDTO} from '../shared/model/payment-dto.model';
import {NewTicket} from '../shared/model/new-ticket.model';
import {NewReservationAndPaymentDTO} from '../shared/model/new-reservation-and-payment.model';
import {NewReservation} from '../shared/model/new-reservation.model';

describe('ReservationApiService', () => {
  let reservationApiService: ReservationApiService;
  let httpClient: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    const httpSpy = jasmine.createSpyObj('HttpClient', ['get', 'post', 'delete']);

    TestBed.configureTestingModule({
      providers: [ReservationApiService, {provide: HttpClient, useValue: httpSpy}]
    });
    reservationApiService = TestBed.get(ReservationApiService);
    httpClient = TestBed.get(HttpClient);

  });

  it('should be created', () => {
    const service: ReservationApiService = TestBed.get(ReservationApiService);
    expect(service).toBeTruthy();
  });

  it('should return page (HttpClient called once)', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;
    const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);

    const expectedReservations = [
      new Reservation(1, null, 'eventName', 1, [ticket]),
      new Reservation(1, 'orderId', 'eventName', 1, [ticket])
    ];
    expectedPage.content = expectedReservations as [];

    httpClient.get.and.returnValue(of(expectedPage));

    reservationApiService.getReservations('ALL', 0, 5).subscribe(
      result => expect(result).toEqual(expectedPage, 'expected page'),
      fail
    );

    expect(httpClient.get.calls.count()).toBe(1, 'one call');
  });

  it('should return reservation (HttpClient called once)', () => {
    const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);
    const reservation: Reservation = new Reservation(1, null, 'eventName', 1, [ticket]);

    httpClient.get.and.returnValue(of(reservation));

    reservationApiService.getReservation(1).subscribe(
      result => expect(result).toEqual(reservation, 'expected reservation'),
      fail
    );

    expect(httpClient.get.calls.count()).toBe(1, 'one call');
  });

  it('should cancel reservation (HttpClient called once)', () => {
    const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);
    const reservation: Reservation = new Reservation(1, null, 'eventName', 1, [ticket]);

    httpClient.delete.and.returnValue(of(reservation));

    reservationApiService.cancelReservation(1).subscribe(
      result => expect(result).toEqual(reservation, 'expected reservation'),
      fail
    );

    expect(httpClient.delete.calls.count()).toBe(1, 'one call');
  });

  it('should create payment for reservation (HttpClient called once)', () => {
    const paymentDTO: PaymentDTO = new PaymentDTO('paymentID', 'payerID');

    httpClient.post.and.returnValue(of(paymentDTO));

    reservationApiService.payReservationCreatePayment(1).subscribe(
      result => expect(result).toEqual(paymentDTO, 'expected paymentDTO'),
      fail
    );

    expect(httpClient.post.calls.count()).toBe(1, 'one call');
  });

  it('should execute payment for reservation (HttpClient called once)', () => {
    const paymentDTO: PaymentDTO = new PaymentDTO('paymentID', 'payerID');
    const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);
    const reservation: Reservation = new Reservation(1, 'paymentID', 'eventName', 1, [ticket]);

    httpClient.post.and.returnValue(of(reservation));

    reservationApiService.payReservationExecutePayment(paymentDTO, 1).subscribe(
      result => expect(result).toEqual(reservation, 'expected reservation'),
      fail
    );

    expect(httpClient.post.calls.count()).toBe(1, 'one call');
  });

  it('should create payment for reservation creation (HttpClient called once)', () => {
    const paymentDTO: PaymentDTO = new PaymentDTO('paymentID', 'payerID');
    const newReservation: NewReservation = new NewReservation();

    httpClient.post.and.returnValue(of(paymentDTO));

    reservationApiService.createAndPayReservationCreatePayment(newReservation).subscribe(
      result => expect(result).toEqual(paymentDTO, 'expected paymentDTO'),
      fail
    );

    expect(httpClient.post.calls.count()).toBe(1, 'one call');
  });

  it('should execute payment for reservation creation (HttpClient called once)', () => {
    const paymentDTO: PaymentDTO = new PaymentDTO('paymentID', 'payerID');
    const newReservationAndPaymentDTO: NewReservationAndPaymentDTO = new NewReservationAndPaymentDTO();
    const ticket: Ticket = new Ticket(1, 1, 1, 'seatGroupName', 10, [new Date()]);
    const reservation: Reservation = new Reservation(1, 'paymentID', 'eventName', 1, [ticket]);

    httpClient.post.and.returnValue(of(reservation));

    reservationApiService.createAndPayReservationExecutePayment(newReservationAndPaymentDTO).subscribe(
      result => expect(result).toEqual(reservation, 'expected reservation'),
      fail
    );

    expect(httpClient.post.calls.count()).toBe(1, 'one call');
  });
});
