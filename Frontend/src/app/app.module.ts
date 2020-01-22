import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexLayoutModule} from '@angular/flex-layout';
import {DashboardModule} from './dashboard/dashboard.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {CoreModule} from './core/core.module';
import {LocationModule} from './location/location.module';
import {ToolbarModule} from './toolbar/toolbar.module';
import {ReportsModule} from './reports/reports.module';
import {AuthModule} from "./auth/auth.module";
import {TokenInterceptor} from "./auth/token-interceptor";

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
    ToolbarModule,
    ReportsModule,
    AuthModule
  ],
  providers: [{provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
