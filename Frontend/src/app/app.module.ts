import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexLayoutModule} from '@angular/flex-layout';
import {DashboardModule} from './dashboard/dashboard.module';
import {HttpClientModule} from '@angular/common/http';
import {CoreModule} from './core/core.module';
import {LocationModule} from './location/location.module';
import {EventModule} from './event/event.module';
import {ToolbarModule} from './toolbar/toolbar.module';
import {ReportsModule} from './reports/reports.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    DashboardModule,
    HttpClientModule,
    CoreModule,
    FlexLayoutModule,
    LocationModule,
    EventModule,
    ToolbarModule,
    ReportsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
