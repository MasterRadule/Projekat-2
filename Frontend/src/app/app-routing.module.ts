import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';


const routes: Routes = [
  {
    path: 'dashboard/:content',
    component: DashboardComponent
  },
  {
    path: 'dashboard',
    redirectTo: '/dashboard/events',
    pathMatch: 'full'
  },
  {
    path: '',
    redirectTo: '/dashboard/events',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
