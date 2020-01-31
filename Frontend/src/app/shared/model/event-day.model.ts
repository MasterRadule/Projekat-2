import {Event} from './event.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class EventDay extends Serializable {
  @SerializeProperty({
    map: 'id'
  })
  private _id: number;
  @SerializeProperty({
    map: 'date'
  })
  private _date: string;
  private _event: Event;

  constructor(id: number, date: string, event: Event) {
    super();
    this._id = id;
    this._date = date;
    this._event = event;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get date(): string {
    return this._date;
  }

  set date(value: string) {
    this._date = value;
  }

  get event(): Event {
    return this._event;
  }

  set event(value: Event) {
    this._event = value;
  }
}
