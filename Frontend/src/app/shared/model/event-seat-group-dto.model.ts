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

  constructor(seatGroupID: number, price: number) {
    super();
    this._seatGroupID = seatGroupID;
    this._price = price;
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
}