import {EventDay} from './event-day.model';

export class Event {
  private _id: number;
  private _name: string;
  private _description: string;
  private _category: string;
  private _activeForReservations: boolean;
  private _cancelled: boolean;
  private _reservationDeadlineDays: number;
  private _maxTicketsPerReservation: number;
  private _eventDays: EventDay[];

  constructor(id: number, name: string, description: string, category: string, activeForReservations: boolean,
              cancelled: boolean, reservationDeadlineDays: number, maxTicketsPerReservation: number, eventDays: EventDay[]) {
    this._id = id;
    this._name = name;
    this._description = description;
    this._category = category;
    this._activeForReservations = activeForReservations;
    this._cancelled = cancelled;
    this._reservationDeadlineDays = reservationDeadlineDays;
    this._maxTicketsPerReservation = maxTicketsPerReservation;
    this._eventDays = eventDays;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get description(): string {
    return this._description;
  }

  set description(value: string) {
    this._description = value;
  }

  get category(): string {
    return this._category;
  }

  set category(value: string) {
    this._category = value;
  }

  get activeForReservations(): boolean {
    return this._activeForReservations;
  }

  set activeForReservations(value: boolean) {
    this._activeForReservations = value;
  }

  get cancelled(): boolean {
    return this._cancelled;
  }

  set cancelled(value: boolean) {
    this._cancelled = value;
  }

  get reservationDeadlineDays(): number {
    return this._reservationDeadlineDays;
  }

  set reservationDeadlineDays(value: number) {
    this._reservationDeadlineDays = value;
  }

  get maxTicketsPerReservation(): number {
    return this._maxTicketsPerReservation;
  }

  set maxTicketsPerReservation(value: number) {
    this._maxTicketsPerReservation = value;
  }

  get eventDays(): EventDay[] {
    return this._eventDays;
  }

  set eventDays(value: EventDay[]) {
    this._eventDays = value;
  }
}
