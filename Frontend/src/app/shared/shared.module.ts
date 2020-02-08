import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import { JoinEventDaysPipe } from './pipes/join-event-days.pipe';
import { PaypalButtonComponent } from './paypal-button/paypal-button.component';


@NgModule({
    declarations: [JoinEventDaysPipe, PaypalButtonComponent],
    exports: [
        JoinEventDaysPipe,
        PaypalButtonComponent
    ],
    imports: [
        CommonModule
    ]
})
export class SharedModule {
}
