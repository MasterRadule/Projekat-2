import {EventApiService} from './event-api.service';
import {Page} from '../shared/model/page.model';
import {of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {TestBed} from '@angular/core/testing';
import {Event} from '../shared/model/event.model';
import {SearchEventsDTO} from '../shared/model/search-events-dto.model';

describe('EventApiService', () => {
  let eventApiService: EventApiService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    const spy = jasmine.createSpyObj('HttpClient', ['get']);

    TestBed.configureTestingModule({
      providers: [
        EventApiService,
        {provide: HttpClient, useValue: spy}
      ]
    });
    eventApiService = TestBed.get(EventApiService);
    httpClientSpy = TestBed.get(HttpClient);
  });

  it('should be created', () => {
    expect(eventApiService).toBeTruthy();
  });

  it('should return page (HttpClient called once) - getEvents method', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;

    const expectedEvents = [
      new Event(1, 'Event 1', 'Description', 'Sport', true, false, 1, 1, []),
      new Event(2, 'Event 2', 'Description', 'Sport', true, false, 1, 1, [])
    ];
    expectedPage.content = expectedEvents as [];

    httpClientSpy.get.and.returnValue(of(expectedPage));

    eventApiService.getEvents(0, 5).subscribe(
      result => expect(result).toEqual(expectedPage, 'expectedPage'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

  it('should return page (HttpClient called once) - searchEvents method', () => {
    const expectedPage: Page = new Page();
    expectedPage.number = 0;
    expectedPage.size = 2;
    expectedPage.totalElements = 30;
    expectedPage.first = true;
    expectedPage.last = false;
    expectedPage.totalPages = 6;

    const expectedEvents = [
      new Event(1, 'Event 1', 'Description', 'Sport', true, false, 1, 1, []),
      new Event(2, 'Event 2', 'Description', 'Sport', true, false, 1, 1, []),
      new Event(3, 'Event 3', 'Description', 'Music', true, false, 1, 1, [])
    ];
    expectedPage.content = expectedEvents as [];
    const parameters: SearchEventsDTO = new SearchEventsDTO('', null, null, '', '');

    httpClientSpy.get.and.returnValue(of(expectedPage));

    eventApiService.searchEvents(parameters, 0, 5).subscribe(
      result => expect(result).toEqual(expectedPage, 'expectedPage'),
      fail
    );

    expect(httpClientSpy.get.calls.count()).toBe(1, 'oneCall');
  });

});
