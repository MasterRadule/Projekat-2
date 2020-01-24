import {EventDay} from './event-day.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class Event extends Serializable {
  @SerializeProperty({
    map: 'id'
  })
  private _id: number;
  @SerializeProperty({
    map: 'name'
  })
  private _name: string;
  @SerializeProperty({
    map: 'description'
  })
  private _description: string;
  @SerializeProperty({
    map: 'category'
  })
  private _category: string;
  @SerializeProperty({
    map: 'activeForReservations'
  })
  private _activeForReservations: boolean;
  @SerializeProperty({
    map: 'cancelled'
  })
  private _cancelled: boolean;
  @SerializeProperty({
    map: 'reservationDeadlineDays'
  })
  private _reservationDeadlineDays: number;
  @SerializeProperty({
    map: 'maxTicketsPerReservation'
  })
  private _maxTicketsPerReservation: number;
  @SerializeProperty({
    map: 'eventDays'
  })
  private _eventDays: EventDay[];

  constructor(id: number, name: string, description: string, category: string, activeForReservations: boolean,
              cancelled: boolean, reservationDeadlineDays: number, maxTicketsPerReservation: number, eventDays: EventDay[]) {
    super();
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
