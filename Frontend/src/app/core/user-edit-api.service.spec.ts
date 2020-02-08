import { TestBed } from '@angular/core/testing';

import { UserEditApiService } from './user-edit-api.service';

describe('UserEditApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: UserEditApiService = TestBed.get(UserEditApiService);
    expect(service).toBeTruthy();
  });
});
