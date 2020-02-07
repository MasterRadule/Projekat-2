import {Component, Input, OnInit} from '@angular/core';
import {Event} from '../../../shared/model/event.model';
import {AuthenticationApiService} from '../../../core/authentication-api.service';

@Component({
  selector: 'app-event-preview',
  templateUrl: './event-preview.component.html',
  styleUrls: ['./event-preview.component.scss']
})
export class EventPreviewComponent implements OnInit {
  @Input() public event: Event;
  private getRole: string = this.authService.getRole();

  constructor(private authService: AuthenticationApiService) {
  }

  ngOnInit() {
  }

  get getEvent(): Event {
    return this.event;
  }

  set setEvent(value: Event) {
    this.event = value;
  }
}
