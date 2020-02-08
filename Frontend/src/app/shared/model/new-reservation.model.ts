import {Ticket} from './ticket.model';
import {NewTicket} from './new-ticket.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class NewReservation extends Serializable {
  @SerializeProperty({
    map: 'eventId'
  })
  private _eventId: number;

  @SerializeProperty({
    map: 'tickets',
    list: true
  })
  private _tickets: NewTicket[];

  constructor(eventId?: number) {
    super();
    this._eventId = eventId;
    this.tickets = [];
  }

  get eventId(): number {
    return this._eventId;
  }

  set eventId(value: number) {
    this._eventId = value;
  }

  get tickets(): NewTicket[] {
    return this._tickets;
  }

  set tickets(value: NewTicket[]) {
    this._tickets = value;
  }
}
