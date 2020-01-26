import { TestBed } from '@angular/core/testing';

import { ReservationApiService } from './reservation-api.service';

describe('ReservationApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ReservationApiService = TestBed.get(ReservationApiService);
    expect(service).toBeTruthy();
  });
});
