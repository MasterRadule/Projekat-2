import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class ReportDTO extends Serializable {
  @SerializeProperty({
    map: 'tickets',
    list: true
  })
  private _tickets: number[];
  @SerializeProperty({
    map: 'earnings',
    list: true
  })
  private _earnings: number[];
  @SerializeProperty({
    map: 'labels',
    list: true
  })
  private _labels: string[];


  get tickets(): number[] {
    return this._tickets;
  }

  set tickets(value: number[]) {
    this._tickets = value;
  }

  get earnings(): number[] {
    return this._earnings;
  }

  set earnings(value: number[]) {
    this._earnings = value;
  }

  get labels(): string[] {
    return this._labels;
  }

  set labels(value: string[]) {
    this._labels = value;
  }
}

