export class SearchEventsDTO {
	private _name: string;
    private _locationID: number;
    private _category: string;
    private _fromDate: string;
    private _endDate: string;

    constructor(name: string, locationID: number, category: string,
    			fromDate: string, endDate: string) {
    	this._name = name;
    	this._locationID = locationID;
    	this._category = category;
    	this._fromDate = fromDate;
    	this._endDate = endDate;
    }

    get name() : string {
    	return this._name;
    }

    set name(name: string) {
    	this._name = name;
    }

    get locationID() : number {
    	return this._locationID;
    }

    set locationID(locationID: number) {
    	this._locationID = locationID;
    }

    get category() : string {
    	return this._category;
    }

    set category(category: string) {
    	this._category = category;
    }

    get fromDate() : string {
    	return this._fromDate;
    }

    set fromDate(fromDate: string) {
    	this._fromDate = fromDate;
    }

    get endDate() : string {
    	return this._endDate;
    }

    set endDate(endDate: string) {
    	this._endDate = endDate;
    }
}