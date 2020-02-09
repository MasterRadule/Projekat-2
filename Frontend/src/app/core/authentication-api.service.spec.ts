import { TestBed } from '@angular/core/testing';

import { AuthenticationApiService } from './authentication-api.service';
import {HttpClient} from '@angular/common/http';
import {of} from 'rxjs';

describe('AuthenticationApiService', () => {
  let authenticationApiService: AuthenticationApiService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('HttpClient', ['get', 'post', 'put']);

    TestBed.configureTestingModule({
      providers: [
        AuthenticationApiService,
        {provide: HttpClient, useValue: spy}
      ]
    });
    authenticationApiService = TestBed.get(AuthenticationApiService);
    httpClientSpy = TestBed.get(HttpClient);
  });


  it('should be created', () => {
    expect(authenticationApiService).toBeTruthy();

  });

  it('should login (HttpClient called once)', () => {
    const userDto: any = {
      email: 'vhkof0165@nowhere.com',
      password: '123'
    };
    httpClientSpy.post.and.returnValue(of('&1234'));
    authenticationApiService.login(userDto).subscribe(data => {
      expect(data).toEqual('&1234');
    });

    expect(httpClientSpy.post.calls.count()).toBe(1, 'oneCall');
  });


});
