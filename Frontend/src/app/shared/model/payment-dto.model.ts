import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class PaymentDTO extends Serializable{
  @SerializeProperty({
    map: 'paymentID'
  })
  private _paymentID: string;
  @SerializeProperty({
    map: 'payerID'
  })
  private _payerID: string;

  constructor(paymentID: string, payerID: string) {
    super();
    this._paymentID = paymentID;
    this._payerID = payerID;
  }

  get paymentID(): string {
    return this._paymentID;
  }

  set paymentID(value: string) {
    this._paymentID = value;
  }

  get payerID(): string {
    return this._payerID;
  }

  set payerID(value: string) {
    this._payerID = value;
  }
}
