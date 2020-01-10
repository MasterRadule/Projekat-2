export class Location {
  private _id: number;
  private _name: string;
  private _latitude: number;
  private _longitude: number;
  private _disabled: boolean;

  constructor(id: number, name: string, latitude: number, longitude: number, disabled: boolean) {
    this._id = id;
    this._name = name;
    this._latitude = latitude;
    this._longitude = longitude;
    this._disabled = disabled;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get name(): string {
    return this._name;
  }

  set name(value: string) {
    this._name = value;
  }

  get latitude(): number {
    return this._latitude;
  }

  set latitude(value: number) {
    this._latitude = value;
  }

  get longitude(): number {
    return this._longitude;
  }

  set longitude(value: number) {
    this._longitude = value;
  }

  get disabled(): boolean {
    return this._disabled;
  }

  set disabled(value: boolean) {
    this._disabled = value;
  }
}
