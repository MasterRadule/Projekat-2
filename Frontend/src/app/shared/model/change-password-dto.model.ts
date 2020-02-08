import {Serializable, Serialize, SerializeProperty} from 'ts-serializer';

@Serialize({})
export class ChangePasswordDTO extends Serializable {

  @SerializeProperty({
    map: 'oldPassword'
  })
  private oldPassword: string;

  @SerializeProperty({
    map: 'password'
  })
  private password: string;

  @SerializeProperty({
    map: 'repeatedPassword'
  })
  private repeatedPassword: string;


  getOldPassword(): string {
    return this.oldPassword;
  }

  setOldPassword(value: string) {
    this.oldPassword = value;
  }

  getPassword(): string {
    return this.password;
  }

  setPassword(value: string) {
    this.password = value;
  }

  getRepeatedPassword(): string {
    return this.repeatedPassword;
  }

  setRepeatedPassword(value: string) {
    this.repeatedPassword = value;
  }

  constructor(oldPassword: string, password: string, repeatedPassword: string) {
    super();
    this.oldPassword = oldPassword;
    this.password = password;
    this.repeatedPassword = repeatedPassword;
  }

}
