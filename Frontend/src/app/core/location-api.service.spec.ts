import {LocationApiService} from './location-api.service';
import {Location} from '../shared/model/location.model';
import {Page} from '../shared/model/page.model';
import {of} from 'rxjs';
import {SeatGroup} from '../shared/model/seat-group.model';

describe('LocationApiService', () => {
  let locationApiService: LocationApiService;
  let httpClientSpy;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put']);
    locationApiService = new LocationApiService(httpClientSpy as any);
  });

  it('should be created', () => {
    expect(locationApiService).toBeTruthy();
  });

  it('should return page (HttpClient called once)', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;

    const expectedLocations = [
      new Location(0, 'Spens', 54.0, 30.0, false),
      new Location(1, 'Promenada', 54.54, 20.0, false)
    ];
    expectedPage.content = expectedLocations as [];

    httpClientSpy.get.and.returnValue(of(expectedPage));

    locationApiService.getLocations(0, 5).subscribe(
      result => expect(result).toEqual(expectedPage, 'expectedPage'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should return location (HttpClient called once)', () => {
    const location: Location = new Location(0, 'Spens', 54.0, 30.0, false);

    httpClientSpy.get.and.returnValue(of(location));

    locationApiService.getLocation(0).subscribe(
      result => expect(result).toEqual(location, 'expectedLocation'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should return location options (HttpClient called once)', () => {
    const expectedLocations: Location[] = [
      new Location(0, 'Spens', 54.0, 30.0, false),
      new Location(1, 'Promenada', 54.54, 20.0, false)
    ];

    httpClientSpy.get.and.returnValue(of(expectedLocations));

    locationApiService.getLocationsOptions().subscribe(
      result => expect(result).toEqual(expectedLocations, 'expectedLocationOptions'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should return page after search (HttpClient called once)', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;

    const expectedLocations = [
      new Location(0, 'Spens', 54.0, 30.0, false),
      new Location(1, 'Promenada', 54.54, 20.0, false)
    ];
    expectedPage.content = expectedLocations as [];

    httpClientSpy.get.and.returnValue(of(expectedPage));

    locationApiService.searchLocations('a', 0, 5).subscribe(
      result => expect(result).toEqual(expectedPage, 'expectedPage'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should create location (HttpClient called once)', () => {
    const locationBeforeSave: Location = new Location(null, 'Spens', 54.0, 30.0, false);
    const locationAfterSave: Location = new Location(0, 'Spens', 54.0, 30.0, false);

    httpClientSpy.post.and.returnValue(of(locationAfterSave));

    locationApiService.createLocation(locationBeforeSave).subscribe(
      result => expect(result).toEqual(locationAfterSave, 'expectedLocation'),
      fail
    );

    expect(httpClientSpy.post.calls.count()).toBe(1, 'oneCall');
  });

  it('should edit location (HttpClient called once)', () => {
    const locationBeforeEdit: Location = new Location(0, 'Spens', 54.0, 30.0, false);
    const locationAfterEdit: Location = new Location(0, 'Spens1', 54.0, 30.0, false);

    httpClientSpy.put.and.returnValue(of(locationAfterEdit));

    locationApiService.editLocation(locationBeforeEdit).subscribe(
      result => expect(result).toEqual(locationAfterEdit, 'expectedLocation'),
      fail
    );

    expect(httpClientSpy.put.calls.count()).toBe(1, 'oneCall');
  });

  it('should get seat groups (HttpClient called once)', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;

    const seatGroup1 = new SeatGroup();
    seatGroup1.xCoordinate = 0.0;
    seatGroup1.angle = 0.0;
    seatGroup1.id = 0;
    seatGroup1.totalSeats = 30;
    seatGroup1.yCoordinate = 0.0;
    seatGroup1.parterre = false;
    seatGroup1.name = 'Balcony';
    seatGroup1.rowsNum = 5;
    seatGroup1.colsNum = 6;

    const seatGroup2 = new SeatGroup();
    seatGroup2.xCoordinate = 30.0;
    seatGroup2.angle = 30.0;
    seatGroup2.id = 1;
    seatGroup2.totalSeats = 50;
    seatGroup2.yCoordinate = 0.0;
    seatGroup2.parterre = true;
    seatGroup2.name = 'Parterre';
    seatGroup2.rowsNum = null;
    seatGroup2.colsNum = null;

    const expectedSeatGroups = [seatGroup1, seatGroup2];
    expectedPage.content = expectedSeatGroups as [];

    httpClientSpy.get.and.returnValue(of(expectedSeatGroups));

    locationApiService.getSeatGroups(0, 0, 5).subscribe(
      result => expect(result).toEqual(expectedSeatGroups, 'expectedSeatGroups'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should get seat group (HttpClient called once)', () => {
    const expectedSeatGroup = new SeatGroup();
    expectedSeatGroup.xCoordinate = 0.0;
    expectedSeatGroup.angle = 0.0;
    expectedSeatGroup.id = 0;
    expectedSeatGroup.totalSeats = 30;
    expectedSeatGroup.yCoordinate = 0.0;
    expectedSeatGroup.parterre = false;
    expectedSeatGroup.name = 'Balcony';
    expectedSeatGroup.rowsNum = 5;
    expectedSeatGroup.colsNum = 6;

    httpClientSpy.get.and.returnValue(of(expectedSeatGroup));

    locationApiService.getSeatGroup(0, 0).subscribe(
      result => expect(result).toEqual(expectedSeatGroup, 'expectedSeatGroup'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should create seat group (HttpClient called once)', () => {
    const seatGroupBeforeSave = new SeatGroup();
    seatGroupBeforeSave.xCoordinate = 0.0;
    seatGroupBeforeSave.angle = 0.0;
    seatGroupBeforeSave.id = null;
    seatGroupBeforeSave.totalSeats = 30;
    seatGroupBeforeSave.yCoordinate = 0.0;
    seatGroupBeforeSave.parterre = false;
    seatGroupBeforeSave.name = 'Balcony';
    seatGroupBeforeSave.rowsNum = 5;
    seatGroupBeforeSave.colsNum = 6;

    const seatGroupAfterSave = new SeatGroup();
    seatGroupAfterSave.xCoordinate = 0.0;
    seatGroupAfterSave.angle = 0.0;
    seatGroupAfterSave.id = 0;
    seatGroupAfterSave.totalSeats = 30;
    seatGroupAfterSave.yCoordinate = 0.0;
    seatGroupAfterSave.parterre = false;
    seatGroupAfterSave.name = 'Balcony';
    seatGroupAfterSave.rowsNum = 5;
    seatGroupAfterSave.colsNum = 6;

    httpClientSpy.post.and.returnValue(of(seatGroupAfterSave));

    locationApiService.createSeatGroup(0, seatGroupBeforeSave).subscribe(
      result => expect(result).toEqual(seatGroupAfterSave, 'expectedSeatGroupAfterSave'),
      fail
    );

    expect(httpClientSpy.post.calls.count()).toBe(1, 'oneCall');
  });

  it('should edit seat group position (HttpClient called once)', () => {
    const seatGroupBeforeEdit = new SeatGroup();
    seatGroupBeforeEdit.xCoordinate = 0.0;
    seatGroupBeforeEdit.angle = 0.0;
    seatGroupBeforeEdit.id = null;
    seatGroupBeforeEdit.totalSeats = 30;
    seatGroupBeforeEdit.yCoordinate = 0.0;
    seatGroupBeforeEdit.parterre = false;
    seatGroupBeforeEdit.name = 'Balcony';
    seatGroupBeforeEdit.rowsNum = 5;
    seatGroupBeforeEdit.colsNum = 6;

    const seatGroupAfterEdit = new SeatGroup();
    seatGroupAfterEdit.xCoordinate = 30.0;
    seatGroupAfterEdit.angle = 30.0;
    seatGroupAfterEdit.id = 0;
    seatGroupAfterEdit.totalSeats = 30;
    seatGroupAfterEdit.yCoordinate = 50.0;
    seatGroupAfterEdit.parterre = false;
    seatGroupAfterEdit.name = 'Balcony';
    seatGroupAfterEdit.rowsNum = 5;
    seatGroupAfterEdit.colsNum = 6;

    httpClientSpy.put.and.returnValue(of(seatGroupAfterEdit));

    locationApiService.editSeatGroupPosition(0, seatGroupBeforeEdit).subscribe(
      result => expect(result).toEqual(seatGroupAfterEdit, 'expectedSeatGroupAfterEdit'),
      fail
    );

    expect(httpClientSpy.put.calls.count()).toBe(1, 'oneCall');
  });
});
