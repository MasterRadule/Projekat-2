import { Injectable } from '@angular/core';
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router} from '@angular/router';
import { Observable } from 'rxjs';
import {AuthenticationApiService} from '../../core/authentication-api.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(
    private authenticationApiService: AuthenticationApiService,
    private router: Router
  ) { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const expectedRoles: string[] = next.data.expectedRoles.split('|');
    const result: boolean = expectedRoles.includes(this.authenticationApiService.getRole());
    if (! result) { this.router.navigate(['']); }
    return result;
  }

}
