import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class ReportDTO extends Serializable {
  @SerializeProperty({
    map: 'date'
  })
  private _date: Date;
  @SerializeProperty({
    map: 'ticketCount'
  })
  private _ticketCount: number;
  @SerializeProperty({
    map: 'earnings'
  })
  private _earnings: number;


  get date(): Date {
    return this._date;
  }

  set date(value: Date) {
    this._date = value;
  }

  get ticketCount(): number {
    return this._ticketCount;
  }

  set ticketCount(value: number) {
    this._ticketCount = value;
  }

  get earnings(): number {
    return this._earnings;
  }

  set earnings(value: number) {
    this._earnings = value;
  }
}
