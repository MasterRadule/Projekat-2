export class SeatGroup {
  private _id: number;
  private _rowsNum: number;
  private _colsNum: number;
  private _parterre: boolean;
  private _xCoordinate: number;
  private _yCoordinate: number;
  private _totalSeats: number;
  private _name: string;


  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get rowsNum(): number {
    return this._rowsNum;
  }

  set rowsNum(value: number) {
    this._rowsNum = value;
  }

  get colsNum(): number {
    return this._colsNum;
  }

  set colsNum(value: number) {
    this._colsNum = value;
  }

  get parterre(): boolean {
    return this._parterre;
  }

  set parterre(value: boolean) {
    this._parterre = value;
  }

  get xCoordinate(): number {
    return this._xCoordinate;
  }

  set xCoordinate(value: number) {
    this._xCoordinate = value;
  }

  get yCoordinate(): number {
    return this._yCoordinate;
  }

  set yCoordinate(value: number) {
    this._yCoordinate = value;
  }

  get totalSeats(): number {
    return this._totalSeats;
  }

  set totalSeats(value: number) {
    this._totalSeats = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }
}
