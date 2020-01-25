import {Ticket} from './ticket.model';

export class Reservation {
  private _id: number;

  private _orderId: string;

  private _eventName: string;

  private _eventId: number;

  private _tickets: Ticket[];

  constructor(id: number, orderId: string, eventName: string, eventId: number, tickets: Ticket[]) {
    this._id = id;
    this._orderId = orderId;
    this._eventName = eventName;
    this._eventId = eventId;
    this._tickets = tickets;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get orderId(): string {
    return this._orderId;
  }

  set orderId(value: string) {
    this._orderId = value;
  }

  get eventName(): string {
    return this._eventName;
  }

  set eventName(value: string) {
    this._eventName = value;
  }

  get eventId(): number {
    return this._eventId;
  }

  set eventId(value: number) {
    this._eventId = value;
  }

  get tickets(): Ticket[] {
    return this._tickets;
  }

  set tickets(value: Ticket[]) {
    this._tickets = value;
  }
}
