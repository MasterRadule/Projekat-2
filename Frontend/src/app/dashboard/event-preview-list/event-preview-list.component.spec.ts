import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {EventPreviewListComponent} from './event-preview-list.component';
import {EventPreviewComponent} from './event-preview/event-preview.component';
import {EventApiService} from '../../core/event-api.service';
import {LocationApiService} from '../../core/location-api.service';
import {FormsModule} from '@angular/forms';
import {Location} from '../../shared/model/location.model';
import {Event} from '../../shared/model/event.model';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatIconModule,
  MatSnackBar,
  MatSnackBarModule,
  MatFormFieldModule,
  MatSelectModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  DateAdapter,
  MatInputModule,
  MatTooltipModule,
  PageEvent
} from '@angular/material';
import {FlexModule} from '@angular/flex-layout';
import {CoreModule} from '../../core/core.module';
import {RouterTestingModule} from '@angular/router/testing';
import {By} from '@angular/platform-browser';
import { of } from 'rxjs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {Router} from '@angular/router';
import {Page} from '../../shared/model/page.model';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('EventPreviewListComponent', () => {
  let component: EventPreviewListComponent;
  let fixture: ComponentFixture<EventPreviewListComponent>;
  let eventApiServiceSpy: jasmine.SpyObj<EventApiService>;
  let locationApiServiceSpy: jasmine.SpyObj<LocationApiService>;
  let router: Router;
  const event1: Event = new Event(1, 'Event 1', 'Description', 'Sport', true, false, 1, 1, []);
  const event2: Event = new Event(2, 'Event 2', 'Description', 'Sport', true, false, 1, 1, []);
  const events: Event[] = [event1, event2];
  const page: Page = new Page();
  page.number = 0;
  page.size = 6;
  page.content = events as [];

  const location1: Location = new Location(1, 'Spens', 44.0, 20.0, false);
  const location2: Location = new Location(2, 'Arena', 54.0, 30.0, false);
  const locations: Location[] = [location1, location2];

  beforeEach(async(() => {
    const spyLocation = jasmine.createSpyObj('LocationApiService', ['getLocationsOptions']);
    const spyEvent = jasmine.createSpyObj('EventApiService', ['getEvents', 'searchEvents']);

    TestBed.configureTestingModule({
      imports: [
        MatSnackBarModule,
        MatIconModule,
        MatCardModule,
        MatButtonModule,
        MatButtonToggleModule,
        BrowserAnimationsModule,
        CoreModule,
        FormsModule,
        MatFormFieldModule,
        MatSelectModule,
        MatDatepickerModule,
        FlexModule,
        MatNativeDateModule,
        MatInputModule,
        MatTooltipModule,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          {
            path: 'dashboard/events/preview',
            component: EventPreviewListComponent,
            pathMatch: 'full'
          }
        ])
      ],
      declarations: [ EventPreviewListComponent, EventPreviewComponent ],
      providers: [
        MatSnackBar,
        {provide: EventApiService, useValue: spyEvent},
        {provide: LocationApiService, useValue: spyLocation},
        {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
        {
          provide: MAT_DATE_FORMATS,
          useValue: {
            parse: {
              dateInput: 'DD.MM.YYYY.',
            },
            display: {
              dateInput: 'DD.MM.YYYY.',
              monthYearLabel: 'YYYY',
              dateA11yLabel: 'LL',
              monthYearA11yLabel: 'YYYY',
            },
          },
        }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EventPreviewListComponent);
    component = fixture.componentInstance;

    eventApiServiceSpy = TestBed.get(EventApiService);
    locationApiServiceSpy = TestBed.get(LocationApiService);
    eventApiServiceSpy.getEvents.and.returnValue(of(page));
    locationApiServiceSpy.getLocationsOptions.and.returnValue(of(locations));
    spyOn(component.eventsPageChanged, 'emit');
    router = TestBed.get(Router);
    router.navigate(['/dashboard/events/preview']);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit the eventsPageChanged event after the call of getEvents method', () => {
    expect(component.eventsPageChanged.emit).toHaveBeenCalled();
  });

  it('should have correct number of app-event-preview components after the call of getEvents method', () => {
    expect(fixture.debugElement.query(By.css('#eventsPreview')).children.length)
      .toEqual(events.length);
  });

  it('should update field page after the call of getEvents method', () => {
    expect(component.page).toEqual(page);
  });

  it('should call method searchEvents after the page is changed', () => {
    const pageEvent: PageEvent = new PageEvent();
    pageEvent.pageIndex = 0;
    pageEvent.pageSize = 3;
    eventApiServiceSpy.searchEvents.and.returnValue(of(page));
    component.pageChanged(pageEvent);

    expect(eventApiServiceSpy.searchEvents.calls.count()).toBe(1, 'oneCall');
  });

  it('should update field page after the page is changed', () => {
    const pageEvent: PageEvent = new PageEvent();
    pageEvent.pageIndex = 0;
    pageEvent.pageSize = 3;
    eventApiServiceSpy.searchEvents.and.returnValue(of(page));
    component.pageChanged(pageEvent);

    expect(component.page.size).toEqual(3);
    expect(component.page.number).toEqual(0);
  });

  it('should emit the eventsPageChanged event after the call of searchEvents method', () => {
    eventApiServiceSpy.searchEvents.and.returnValue(of(page));
    const searchButton = fixture.debugElement.query(By.css('#buttonSearch'));
    searchButton.triggerEventHandler('click', {});

    expect(component.eventsPageChanged.emit).toHaveBeenCalled();
  });

  it('should have correct number of app-event-preview components after the call of searchEvents method', () => {
    eventApiServiceSpy.searchEvents.and.returnValue(of(page));
    const searchButton = fixture.debugElement.query(By.css('#buttonSearch'));
    searchButton.triggerEventHandler('click', {});

    expect(fixture.debugElement.query(By.css('#eventsPreview')).children.length)
      .toEqual(events.length);
  });

  it('should update field page after the call of searchEvents method', () => {
    eventApiServiceSpy.searchEvents.and.returnValue(of(page));
    const searchButton = fixture.debugElement.query(By.css('#buttonSearch'));
    searchButton.triggerEventHandler('click', {});

    expect(component.page).toEqual(page);
  });

  it('should reset searchParameters when search form is reset', () => {
    const resetButton = fixture.debugElement.query(By.css('#buttonReset'));
    component.page.size = 9;
    resetButton.triggerEventHandler('click', {});

    expect(component.searchParameters.name).toEqual('');
    expect(component.searchParameters.locationID).toEqual(null);
    expect(component.searchParameters.category).toEqual(null);
    expect(component.searchParameters.startDate).toEqual('');
    expect(component.searchParameters.endDate).toEqual('');
  });

});
