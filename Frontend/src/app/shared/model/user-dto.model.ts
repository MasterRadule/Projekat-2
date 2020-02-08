import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class UserDTO extends Serializable {

  @SerializeProperty({
    map: 'id'
  })
  private id: number;

  @SerializeProperty({
    map: 'firstName'
  })
  private firstName: string;

  @SerializeProperty({
    map: 'lastName'
  })
  private lastName: string;

  @SerializeProperty({
    map: 'email'
  })
  private email: string;

  @SerializeProperty({
    map: 'password'
  })
  private password: string;

  @SerializeProperty({
    map: 'verified'
  })
  private verified: boolean;

  constructor(id: number, firstName: string, lastName: string, email: string, password: string) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.verified = false;
  }


  getVerified(): boolean {
    return this.verified;
  }

  setVerified(value: boolean) {
    this.verified = value;
  }

  getFirstName(): string {
    return this.firstName;
  }

  setFirstName(firstName: string) {
    this.firstName = firstName;
  }

  getLastName(): string {
    return this.lastName;
  }

  setLastName(lastName: string) {
    this.lastName = lastName;
  }

  getEmail(): string {
    return this.email;
  }

  setEmail(email: string) {
    this.email = email;
  }

  getPassword(): string {
    return this.password;
  }

  setPassword(password: string) {
    this.password = password;
  }

}
