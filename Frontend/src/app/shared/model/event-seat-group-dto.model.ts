import {ReservableSeatGroupDTO} from './reservable-seat-group-dto.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class EventSeatGroupDTO extends Serializable {
  @SerializeProperty({
    map: 'seatGroupID'
  })
  private _seatGroupID: number;
  @SerializeProperty({
    map: 'price'
  })
  private _price: number;
  @SerializeProperty({
    map: 'reservableSeatGroups'
  })
  private _reservableSeatGroups: ReservableSeatGroupDTO[];

  constructor(seatGroupID: number, price: number, reservableSeatGroups: ReservableSeatGroupDTO[]) {
    super();
    this._seatGroupID = seatGroupID;
    this._price = price;
    this._reservableSeatGroups = reservableSeatGroups;
  }

  get seatGroupID(): number {
    return this._seatGroupID;
  }

  set seatGroupID(seatGroupID: number) {
    this._seatGroupID = seatGroupID;
  }

  get price(): number {
    return this._price;
  }

  set price(price: number) {
    this._price = price;
  }

  get reservableSeatGroups(): ReservableSeatGroupDTO[] {
    return this._reservableSeatGroups;
  }

  set reservableSeatGroups(reservableSeatGroups: ReservableSeatGroupDTO[]) {
    this._reservableSeatGroups = reservableSeatGroups;
  }
}
