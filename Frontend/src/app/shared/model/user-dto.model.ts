export class UserDTO {
  private _firstName: string;
  private _lastName: string;
  private _email: string;
  private _password: string;

  constructor(firstName: string, lastName: string, email: string, password: string) {
    this._firstName = firstName;
    this._lastName = lastName;
    this._email = email;
    this._password = password;
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
