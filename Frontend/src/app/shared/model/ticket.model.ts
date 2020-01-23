export class Ticket {
  private _id: number;
  private _rowNum: number;
  private _colNum: number;
  private _seatGroupName: string;
  private _price: number;
  private _eventDays: Date[];

  constructor(id: number, rowNum: number, colNum: number, seatGroupName: string, price: number, eventDays: Date[]) {
    this._id = id;
    this._rowNum = rowNum;
    this._colNum = colNum;
    this._seatGroupName = seatGroupName;
    this._price = price;
    this._eventDays = eventDays;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get rowNum(): number {
    return this._rowNum;
  }

  set rowNum(value: number) {
    this._rowNum = value;
  }

  get colNum(): number {
    return this._colNum;
  }

  set colNum(value: number) {
    this._colNum = value;
  }

  get seatGroupName(): string {
    return this._seatGroupName;
  }

  set seatGroupName(value: string) {
    this._seatGroupName = value;
  }

  get price(): number {
    return this._price;
  }

  set price(value: number) {
    this._price = value;
  }

  get eventDays(): Date[] {
    return this._eventDays;
  }

  set eventDays(value: Date[]) {
    this._eventDays = value;
  }
}
