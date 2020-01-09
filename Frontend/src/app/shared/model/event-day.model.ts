import {Event} from './event.model';

export class EventDay {
  private _id: number;
  private _date: Date;
  private _event: Event;

  constructor(id: number, date: Date, event: Event) {
    this._id = id;
    this._date = date;
    this._event = event;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get date(): Date {
    return this._date;
  }

  set date(value: Date) {
    this._date = value;
  }

  get event(): Event {
    return this._event;
  }

  set event(value: Event) {
    this._event = value;
  }
}
