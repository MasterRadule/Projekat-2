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
    map: 'tickets'
  })
  private _tickets: NewTicket[];

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
