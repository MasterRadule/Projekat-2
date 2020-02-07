import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class SeatDTO extends Serializable {
  @SerializeProperty({
    map: 'id'
  })
  private _id: number;
  @SerializeProperty({
    map: 'rowNum'
  })
  private _rowNum: number;
  @SerializeProperty({
    map: 'colNum'
  })
  private _colNum: number;
  @SerializeProperty({
    map: 'reserved'
  })
  private _reserved: boolean;

  constructor(id: number, rowNum: number, colNum: number, reserved: boolean) {
  	super();
  	this._id = id;
  	this._rowNum = rowNum;
  	this._colNum = colNum;
  	this._reserved = reserved;
  }

  get id(): number {
    return this._id;
  }

  set id(id: number) {
    this._id = id;
  }

  get rowNum(): number {
    return this._rowNum;
  }

  set rowNum(rowNum: number) {
    this._rowNum = rowNum;
  }

  get colNum(): number {
    return this._colNum;
  }

  set colNum(colNum: number) {
    this._colNum = colNum;
  }

  get reserved(): boolean {
    return this._reserved;
  }

  set reserved(reserved: boolean) {
    this._reserved= reserved;
  }

}