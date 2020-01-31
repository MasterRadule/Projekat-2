export class MediaFile {
  private _id: number;
  private _fileType: string;
  private _dataBase64: string;

  constructor(id: number, fileType: string, dataBase64: string) {
    this._id = id;
    this._fileType = fileType;
    this._dataBase64 = dataBase64;
  }

  get id(): number {
    return this._id;
  }

  set id(value: number) {
    this._id = value;
  }

  get fileType(): string {
    return this._fileType;
  }

  set fileType(value: string) {
    this._fileType = value;
  }

  get dataBase64(): string {
    return this._dataBase64;
  }

  set dataBase64(value: string) {
    this._dataBase64 = value;
  }
}
