export class Page {
  private _content: [];
  private _totalElements: number;
  private _totalPages: number;
  private _last: boolean;
  private _first: boolean;
  private _size: number;
  private _number: number;

  get content(): [] {
    return this._content;
  }

  set content(value: []) {
    this._content = value;
  }

  get totalElements(): number {
    return this._totalElements;
  }

  set totalElements(value: number) {
    this._totalElements = value;
  }

  get totalPages(): number {
    return this._totalPages;
  }

  set totalPages(value: number) {
    this._totalPages = value;
  }

  get last(): boolean {
    return this._last;
  }

  set last(value: boolean) {
    this._last = value;
  }

  get first(): boolean {
    return this._first;
  }

  set first(value: boolean) {
    this._first = value;
  }

  get size(): number {
    return this._size;
  }

  set size(value: number) {
    this._size = value;
  }

  get number(): number {
    return this._number;
  }

  set number(value: number) {
    this._number = value;
  }
}
