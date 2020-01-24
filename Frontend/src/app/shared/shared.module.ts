import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import { JoinEventDaysPipe } from './pipes/join-event-days.pipe';


@NgModule({
    declarations: [JoinEventDaysPipe],
    exports: [
        JoinEventDaysPipe
    ],
    imports: [
        CommonModule
    ]
})
export class SharedModule {
}
