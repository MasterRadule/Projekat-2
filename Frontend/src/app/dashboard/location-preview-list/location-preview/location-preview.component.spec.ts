import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {LocationPreviewComponent} from './location-preview.component';
import {LocationApiService} from '../../../core/location-api.service';
import {Location} from '../../../shared/model/location.model';
import {Location as UrlLocation} from '@angular/common';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatIconModule, MatSlideToggleModule,
  MatSnackBar,
  MatSnackBarModule
} from '@angular/material';
import {CoreModule} from '../../../core/core.module';
import {RouterTestingModule} from '@angular/router/testing';
import {By} from '@angular/platform-browser';
import {of} from 'rxjs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LocationComponent} from '../../../location/location.component';
import {LocationModule} from '../../../location/location.module';
import {Router} from '@angular/router';

describe('LocationPreviewComponent', () => {
  let component: LocationPreviewComponent;
  let fixture: ComponentFixture<LocationPreviewComponent>;
  let locationApiServiceSpy: jasmine.SpyObj<LocationApiService>;
  let urlLocation: UrlLocation;
  let router: Router;
  const location: Location = new Location(0, 'Spens', 54.0, 30.0, false);


  beforeEach(async(() => {
    const spy = jasmine.createSpyObj('LocationApiService', ['editLocation']);

    TestBed.configureTestingModule({
      imports: [
        MatSnackBarModule,
        MatIconModule,
        MatCardModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatSlideToggleModule,
        BrowserAnimationsModule,
        CoreModule,
        LocationModule,
        RouterTestingModule.withRoutes([
          {
            path: 'dashboard/locations/:id',
            component: LocationComponent,
            pathMatch: 'full'
          }
        ])
      ],
      declarations: [LocationPreviewComponent],
      providers: [
        MatSnackBar,
        {provide: LocationApiService, useValue: spy}
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationPreviewComponent);
    component = fixture.componentInstance;
    component.location = location;
    locationApiServiceSpy = TestBed.get(LocationApiService);
    urlLocation = TestBed.get(UrlLocation);
    router = TestBed.get(Router);
    router.navigate(['/dashboard/locations/preview']);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.location).toEqual(location);
  });

  it('should have correct location title', () => {
    expect(fixture.debugElement.query(By.css('mat-card-title')).nativeElement.innerText)
      .toEqual(location.name);
  });

  it('should initialize map', () => {
    expect(fixture.debugElement.query(By.css('app-map')).nativeElement).toBeTruthy();
  });

  it('should toggle location status', () => {
    const changedLocation: Location = new Location(0, 'Spens', 54.0, 30.0, true);
    locationApiServiceSpy.editLocation.and.returnValue(of(changedLocation));

    const toggleSlider = fixture.debugElement.query(By.css('mat-slide-toggle'));
    toggleSlider.triggerEventHandler('change', {});
    fixture.detectChanges();

    expect(component.location.disabled).toEqual(changedLocation.disabled);
    expect(locationApiServiceSpy.editLocation.calls.count()).toBe(1, 'oneCall');
  });

  it('should go to location edit page', fakeAsync(() => {
    const editButton = fixture.debugElement.query(By.css('button'));
    editButton.triggerEventHandler('click', {});

    fixture.detectChanges();
    tick();

    expect(urlLocation.path()).toBe(`/dashboard/locations/${location.id}`);
  }));
});
