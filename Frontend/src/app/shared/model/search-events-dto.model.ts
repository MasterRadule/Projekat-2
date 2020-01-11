export class SearchEventsDTO {
	private _name: string;
    private _locationID: number;
    private _category: string;
    private _startDate: string;
    private _endDate: string;

    constructor(name: string, locationID: number, category: string,
    			startDate: string, endDate: string) {
    	this._name = name;
    	this._locationID = locationID;
    	this._category = category;
    	this._startDate = startDate;
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

    get startDate() : string {
    	return this._startDate;
    }

    set startDate(startDate: string) {
    	this._startDate = startDate;
    }

    get endDate() : string {
    	return this._endDate;
    }

    set endDate(endDate: string) {
    	this._endDate = endDate;
    }
}