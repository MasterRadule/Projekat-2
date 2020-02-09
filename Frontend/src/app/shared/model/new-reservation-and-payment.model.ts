import {Ticket} from './ticket.model';
import {NewTicket} from './new-ticket.model';
import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';
import {NewReservation} from './new-reservation.model';
import {PaymentDTO} from './payment-dto.model';

@Serialize({})
export class NewReservationAndPaymentDTO extends Serializable {
  @SerializeProperty({
    map: 'newReservationDTO'
  })
  private _newReservationDTO: NewReservation;

  @SerializeProperty({
    map: 'paymentDTO'
  })
  private _paymentDTO: PaymentDTO;

  constructor(newReservationDTO?: NewReservation, paymentDTO?: PaymentDTO) {
    super();
    this._newReservationDTO = newReservationDTO;
    this._paymentDTO = paymentDTO;
  }

  get newReservationDTO(): NewReservation {
    return this._newReservationDTO;
  }

  set newReservationDTO(value: NewReservation) {
    this._newReservationDTO = value;
  }

  get paymentDTO(): PaymentDTO {
    return this._paymentDTO;
  }

  set paymentDTO(value: PaymentDTO) {
    this._paymentDTO = value;
  }
}
