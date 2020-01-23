import { Component, OnInit, ViewChild} from '@angular/core';
import {Event} from '../shared/model/event.model';
import {EventDay} from '../shared/model/event-day.model';
import {ActivatedRoute, Router} from '@angular/router';
import {EventApiService} from '../core/event-api.service';
import {MatSnackBar} from '@angular/material';
import {MatDialog} from '@angular/material/dialog';
import { AxiomSchedulerEvent, AxiomSchedulerComponent } from 'axiom-scheduler';
import {DialogComponent} from './dialog/dialog.component';
import { colors } from './colors';
import * as moment from 'moment';

@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {
  private event: Event = new Event(null, "", "", null, false, false, 0, 0, null);
  private eventCategories: string[] = ['Music', 'Sport', 'Fair', 'Movie', 'Performance', 'Competition'];
  private events: AxiomSchedulerEvent[] = [];
  private startDate: Date = new Date();
  @ViewChild(AxiomSchedulerComponent, {static: false}) scheduler: AxiomSchedulerComponent;

  constructor(private route: ActivatedRoute, private eventApiService: EventApiService, private snackBar: MatSnackBar,
              private router: Router, private dialog: MatDialog) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.getEvent(params.id);
      }
    });
  }

  getEvent(id: number) {
    this.eventApiService.getEvent(id).subscribe(
      {
        next: (result: Event) => {
          this.event = result;
          this.getEventDays();
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  createOrEditEvent() {
    if (this.event.id) {
      this.event.eventDays = [];
      for (let eventDay of this.events) {
        let date: string = moment(eventDay.from).format('DD.MM.YYYY. HH:mm');
        this.event.eventDays.push(new EventDay(eventDay.data.id, date, null).serialize());
      }
      this.eventApiService.editEvent(this.event).subscribe(
        {
          next: (result: Event) => {
            this.event = result;
            this.snackBar.open('Event edited successfully', 'Dismiss', {
              duration: 3000
            });
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
            this.getEvent(this.event.id);
          }
        }
      );
    } 
    /*else {
      this.eventApiService.createEvent(this.event).subscribe(
        {
          next: (result: Event) => {
            this.snackBar.open('Event created successfully', 'Dismiss', {
              duration: 3000
            });
            this.router.navigate(['/dashboard/events/', result.id]).then(r => {
            });
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
          }
        }
      );
    }*/
  }

  getEventDays() {
    for (let ev of this.event.eventDays) {
       let from :Date = moment(ev.date, "DD.MM.YYYY. HH:mm").toDate();
       let to: Date = moment(ev.date, "DD.MM.YYYY. HH:mm").set({hour:23, minute:59, second:59}).toDate();
       this.events.push(new AxiomSchedulerEvent("Event day", from, to, {"id":ev.id}, colors[Math.floor(Math.random()*15)]));
    }
    this.startDate = this.events[0].from;
    this.refreshView();
  }

  openDialog($event, create) {
    const dialogRef = this.dialog.open(DialogComponent);
    if (create) {
        dialogRef.componentInstance.createMode = true;
        dialogRef.afterClosed().subscribe(result => {
        if (result !== "" && result !== undefined) {
          let time = result.time.split(":");
          let from: Date = result.date.set({hour:time[0], minute:time[1]}).toDate();
          let to: Date = result.date.set({hour:23, minute:59, second:59}).toDate();
          this.events.push(new AxiomSchedulerEvent("Event day", from, to, {"id":null}, colors[Math.floor(Math.random()*15)]));
          this.refreshView();
        }
      });
    }
    else {
      dialogRef.componentInstance.dateTime = {"date":$event.from, 
            "time":moment($event.from.toString()).format("HH:mm")};
      dialogRef.componentInstance.createMode = false;
      dialogRef.afterClosed().subscribe(result => {
        if (result !== "" && result !== undefined) {
          console.log(result);
          let time = result.time.split(":");
          let from: Date = moment(result.date).set({hour:time[0], minute:time[1]}).toDate();
          let to: Date = moment(result.date).set({hour:23, minute:59, second:59}).toDate();
          let index = this.events.indexOf($event);
          this.events[index] = new AxiomSchedulerEvent("Event day", from, to, $event.data, $event.color);
          this.refreshView();
        }
      });
    }
  }

  removeEventDay($event) {
    let index = this.events.indexOf($event);
    this.events.splice(index, 1);
    this.refreshView();
  }

  refreshView() : void {
    this.startDate = this.events[0].from;
    this.scheduler.refreshScheduler();
  }

}
