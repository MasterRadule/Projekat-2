import {NewTicket} from './new-ticket.model';

export class NewTicketDetailed extends NewTicket {
  private _rowNum: number;
  private _colNum: number;
  private _seatGroupName: string;
  private _price: number;
  private _date: string;

  constructor(reservableSeatGroupId: number, seatId: number, allDayTicket: boolean, rowNum: number,
              colNum: number, seatGroupName: string, price: number, date: string) {
    super(reservableSeatGroupId, seatId, allDayTicket);
    this._rowNum = rowNum;
    this._colNum = colNum;
    this._seatGroupName = seatGroupName;
    this._price = price;
    this._date = date;
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

  get date(): string {
    return this._date;
  }

  set date(value: string) {
    this._date = value;
  }
}
