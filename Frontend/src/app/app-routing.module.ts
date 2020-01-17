import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {LocationComponent} from './location/location.component';
import {ReportsComponent} from './reports/reports.component';


const routes: Routes = [
  {
    path: 'dashboard/:content/preview',
    component: DashboardComponent
  },
  {
    path: 'dashboard/locations/:id',
    component: LocationComponent,
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
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    redirectTo: '/dashboard/events/preview',
    pathMatch: 'full'
  },
  {
    path: '',
    redirectTo: '/dashboard/events/preview',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
