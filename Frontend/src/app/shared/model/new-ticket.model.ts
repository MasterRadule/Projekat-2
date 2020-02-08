import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class NewTicket extends Serializable {
  @SerializeProperty({
    map: 'reservableSeatGroupId'
  })
  private _reservableSeatGroupId: number;

  @SerializeProperty({
    map: 'seatId'
  })
  private _seatId: number;

  @SerializeProperty({
    map: 'allDayTicket'
  })
  private _allDayTicket: boolean;

  constructor(reservableSeatGroupId: number, seatId: number, allDayTicket: boolean) {
    super();
    this.reservableSeatGroupId = reservableSeatGroupId;
    this.seatId = seatId;
    this.allDayTicket = allDayTicket;
  }

  get reservableSeatGroupId(): number {
    return this._reservableSeatGroupId;
  }

  set reservableSeatGroupId(value: number) {
    this._reservableSeatGroupId = value;
  }

  get seatId(): number {
    return this._seatId;
  }

  set seatId(value: number) {
    this._seatId = value;
  }

  get allDayTicket(): boolean {
    return this._allDayTicket;
  }

  set allDayTicket(value: boolean) {
    this._allDayTicket = value;
  }
}
