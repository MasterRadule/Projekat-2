import {SeatDTO} from './seat-dto.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class ReservableSeatGroupDTO extends Serializable {
  @SerializeProperty({
    map: 'id'
  })
  private _id: number;
  @SerializeProperty({
    map: 'esgID'
  })
  private _esgID: number;
  @SerializeProperty({
    map: 'eventDayID'
  })
  private _eventDayID: number;
  @SerializeProperty({
    map: 'seats'
  })
  private _seats: SeatDTO[];
  @SerializeProperty({
    map: 'freeSeats'
  })
  private _freeSeats: number;

  constructor(id: number, esgID: number, eventDayID: number, seats: SeatDTO[], freeSeats: number) {
    super();
    this._id = id;
    this._esgID = esgID;
    this._eventDayID = eventDayID;
    this._seats = seats;
    this._freeSeats = freeSeats;
  }

  get id(): number {
    return this._id;
  }

  set id(id: number) {
    this._id = id;
  }

  get esgID(): number {
    return this._esgID;
  }

  set esgID(esgID: number) {
    this._esgID = esgID;
  }

  get eventDayID(): number {
    return this._eventDayID;
  }

  set eventDayID(eventDayID: number) {
    this._eventDayID = eventDayID;
  }

  get seats(): SeatDTO[] {
    return this._seats;
  }

  set seats(seats: SeatDTO[]) {
    this._seats = seats;
  }

  get freeSeats(): number {
    return this._freeSeats;
  }

  set freeSeats(freeSeats: number) {
    this._freeSeats = freeSeats;
  }
}
