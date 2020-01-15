import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class ReportRequestDTO extends Serializable {
  @SerializeProperty({
    map: 'startDate'
  })
  private _startDate: number;
  @SerializeProperty({
    map: 'endDate'
  })
  private _endDate: number;
  @SerializeProperty({
    map: 'locationId',
    optional: true
  })
  private _locationId: number;
  @SerializeProperty({
    map: 'eventId',
    optional: true
  })
  private _eventId: number;


  constructor(startDate: number, endDate: number, locationId?: number, eventId?: number) {
    super();
    this._startDate = startDate;
    this._endDate = endDate;
    this._locationId = locationId;
    this._eventId = eventId;
  }

  get startDate(): number {
    return this._startDate;
  }

  set startDate(value: number) {
    this._startDate = value;
  }

  get endDate(): number {
    return this._endDate;
  }

  set endDate(value: number) {
    this._endDate = value;
  }

  get locationId(): number {
    return this._locationId;
  }

  set locationId(value: number) {
    this._locationId = value;
  }

  get eventId(): number {
    return this._eventId;
  }

  set eventId(value: number) {
    this._eventId = value;
  }
}
