import {Routes} from '@angular/router';
import {DashboardComponent} from '../dashboard/dashboard.component';
import {LocationComponent} from '../location/location.component';
import {ReportsComponent} from '../reports/reports.component';
import {LoginComponent} from '../auth/components/login/login.component';
import {RegisterComponent} from '../auth/components/register/register.component';
import {ReservationComponent} from '../reservation/reservation.component';
import {EventComponent} from '../event/event.component';
import {UserEditComponent} from '../user-edit/myProfile/user-edit.component';
import {ChangePasswordComponent} from '../user-edit/change-password/change-password.component';
import {RoleGuard} from '../auth/guards/role.guard';

export const routes: Routes = [
  {
    path: 'dashboard/:content/preview',
    component: DashboardComponent,
  },
  {
    path: 'dashboard/events/:id',
    component: EventComponent,
    pathMatch: 'full'
  },
  {
    path: 'dashboard/locations/:id',
    component: LocationComponent,
    pathMatch: 'full'
  },
  {
    path: 'dashboard/reservations/:id',
    component: ReservationComponent,
    pathMatch: 'full',
    canActivate: [RoleGuard],
    data: {expectedRoles: 'ROLE_USER'}
  },
  {
    path: 'dashboard/events',
    component: EventComponent,
    pathMatch: 'full'
  },
  {
    path: 'dashboard/locations',
    component: LocationComponent,
    pathMatch: 'full'
  },
  {
    path: 'dashboard/reports',
    component: ReportsComponent,
    pathMatch: 'full',
    canActivate: [RoleGuard],
    data: {expectedRoles: 'ROLE_ADMIN'}
  },
  {
    path: 'dashboard',
    redirectTo: '/dashboard/events/preview',
    pathMatch: 'full'
  },
  {
    path: 'myProfile',
    component: UserEditComponent,
    canActivate: [RoleGuard],
    data: {expectedRoles: 'ROLE_ADMIN|ROLE_USER'}
  },
  {
    path: 'changePassword',
    component: ChangePasswordComponent,
    canActivate: [RoleGuard],
    data: {expectedRoles: 'ROLE_ADMIN|ROLE_USER'}
  },
  {
    path: '',
    redirectTo: '/dashboard/events/preview',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [RoleGuard],
    data: {expectedRoles: 'NO_ROLE'}
  },
  {
    path: 'register',
    component: RegisterComponent,
    canActivate: [RoleGuard],
    data: {expectedRoles: 'NO_ROLE'}
  },
  {
    path: '**',
    redirectTo: '/dashboard/events/preview',
  }
];
