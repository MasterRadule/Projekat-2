import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class UserDTO extends Serializable{
  @SerializeProperty({
    map: 'firstName'
  })
  private _firstName: string;

  @SerializeProperty({
    map: 'lastName'
  })
  private _lastName: string;

  @SerializeProperty({
    map: 'email'
  })
  private _email: string;

  @SerializeProperty({
    map: 'password'
  })
  private _password: string;

  @SerializeProperty({
    map: 'verified'
  })
  private _verified: boolean;

  constructor(firstName: string, lastName: string, email: string, password: string) {
    super();
    this._firstName = firstName;
    this._lastName = lastName;
    this._email = email;
    this._password = password;
    this._verified = false;
  }


  get verified(): boolean {
    return this._verified;
  }

  set verified(value: boolean) {
    this._verified = value;
  }

  get firstName():string{
    return this._firstName;
  }

  set firstName(firstName: string){
    this._firstName = firstName;
  }

  get lastName():string{
    return this._lastName;
  }

  set lastName(lastName: string){
    this._lastName = lastName;
  }

  get email():string{
    return this._email;
  }

  set email(email: string){
    this._email = email;
  }

  get password():string{
    return this._password;
  }

  set password(password: string){
    this._password = password;
  }

}
