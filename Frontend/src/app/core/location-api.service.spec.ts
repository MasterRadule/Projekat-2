import {TestBed} from '@angular/core/testing';

import {LocationApiService} from './location-api.service';
import {HttpClientModule} from '@angular/common/http';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {Location} from '../shared/model/location.model';
import {environment} from '../../environments/environment';

describe('LocationApiService', () => {
  let locationApiService: LocationApiService;
  let httpMock: HttpTestingController;
  const baseUrl: string = environment.baseUrl;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule
      ],
      providers: [
        LocationApiService
      ]
    });

    locationApiService = TestBed.get(LocationApiService);
    httpMock = TestBed.get(HttpTestingController);
  });

  it('should be created', () => {
    expect(locationApiService).toBeTruthy();
  });

  it('editLocation() should edit location', () => {
    const locationBeforeEdit: Location = new Location(1, 'Spens', 54.0, 58.0, false);
    const locationAfterEdit: Location = new Location(1, 'Sp', 58.0, 14.0, true);

    locationApiService.editLocation(locationBeforeEdit).subscribe({
        next: (result: Location) => {
          expect(result).toEqual(locationAfterEdit);
        }
      }
    );

    const request = httpMock.expectOne(`${baseUrl}/locations`);
    expect(request.request.method).toBe('PUT');
    request.flush(locationAfterEdit);
  });
});
