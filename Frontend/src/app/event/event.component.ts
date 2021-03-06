import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {Event} from '../shared/model/event.model';
import {EventDay} from '../shared/model/event-day.model';
import {MediaFile} from '../shared/model/media-file.model';
import {ActivatedRoute, Router} from '@angular/router';
import {EventApiService} from '../core/event-api.service';
import {LocationApiService} from '../core/location-api.service';
import {MatMenuTrigger, MatSnackBar} from '@angular/material';
import {MatDialog} from '@angular/material/dialog';
import {AxiomSchedulerEvent, AxiomSchedulerComponent} from 'axiom-scheduler';
import {DialogComponent} from './dialog/dialog.component';
import {colors} from './colors';
import {NgImageSliderComponent} from 'ng-image-slider';
import {FileUploader} from 'ng2-file-upload';
import {Page} from '../shared/model/page.model';
import {SeatGroupsComponent} from '../seat-groups/seat-groups.component';
import {LocationSeatGroupDTO} from '../shared/model/location-seat-group-dto.model';
import {EventSeatGroupDTO} from '../shared/model/event-seat-group-dto.model';
import {AuthenticationApiService} from '../core/authentication-api.service';
import * as moment from 'moment';
import {NewTicketDetailed} from '../shared/model/new-ticket-detailed.model';
import {Reservation} from '../shared/model/reservation.model';
import {NewReservation} from '../shared/model/new-reservation.model';
import {SeatDTO} from '../shared/model/seat-dto.model';

@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {
  private event: Event = new Event(null, '', '', null, false, false, 0, 0, null);
  private eventCategories: string[] = ['Music', 'Sport', 'Fair', 'Movie', 'Performance', 'Competition'];
  private events: AxiomSchedulerEvent[] = [];
  private imageObject: Array<object> = [];
  private uploader: FileUploader;
  private locationsOptions: Location[];
  private locationSeatGroupDTO: LocationSeatGroupDTO = new LocationSeatGroupDTO(null, null, [], '');
  private selectedSeatGroupIndex: number;
  private selectedSeatGroupId = -1;
  private enabledSeatGroup = false;
  private selectedEventDay: EventDay;
  @ViewChild(AxiomSchedulerComponent, {static: false}) private scheduler: AxiomSchedulerComponent;
  @ViewChild('slider', {static: false}) private slider: NgImageSliderComponent;
  @ViewChild(SeatGroupsComponent, {static: false}) private seatGroupComponent: SeatGroupsComponent;
  @ViewChild(MatMenuTrigger, {static: false}) private contextMenu: MatMenuTrigger;
  private contextMenuPosition = {x: '0px', y: '0px'};
  private reservation: NewReservation = new NewReservation();

  private role: string;
  private seatComponentMode: string;

  constructor(private route: ActivatedRoute, private eventApiService: EventApiService,
              private locationApiService: LocationApiService, private authService: AuthenticationApiService,
              private snackBar: MatSnackBar, private router: Router, private dialog: MatDialog) {
    this.role = this.authService.getRole();
    this.seatComponentMode = this.role.concat('_EVENT');
  }

  ngOnInit() {
    this.uploader = new FileUploader({
      url: null
    });

    this.uploader.response.subscribe(res => {
      this.snackBar.open(res, 'Dismiss', {
        duration: 3000
      });
      this.getPicturesAndVideos();
    });

    this.uploader.onAfterAddingFile = (file) => {
      file.withCredentials = false;
    };

    this.getLocationsOptions();

    this.route.params.subscribe(params => {
      if (params.id) {
        this.getEvent(params.id);
      }
    });
  }

  private getEvent(id: number) {
    this.eventApiService.getEvent(id).subscribe(
      {
        next: (result: Event) => {
          this.event = result;
          this.getEventDays();
          this.getPicturesAndVideos();
          this.getEventLocationAndSeatGroups();
          this.reservation = new NewReservation(result.id);
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  private createOrEditEvent() {
    if (this.locationSeatGroupDTO.eventSeatGroups.length === 0) {
      this.snackBar.open('At least one seat group must be enabled', 'Dismiss', {
        duration: 3000
      });
      return;
    }
    this.event.eventDays = [];
    for (const eventDay of this.events) {
      const date: string = moment(eventDay.from).format('DD.MM.YYYY. HH:mm');
      this.event.eventDays.push(new EventDay(eventDay.data.id, date, null).serialize());
    }
    if (this.event.id) {
      this.eventApiService.editEvent(this.event).subscribe(
        {
          next: (result: Event) => {
            this.event = result;
            this.locationSeatGroupDTO.eventID = this.event.id;
            this.saveLocationAndSeatGroups(false);
          },
          error: (message: string) => {
            this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
              duration: 3000
            });
            this.getEvent(this.event.id);
          }
        }
      );
    } else {
      this.eventApiService.createEvent(this.event).subscribe(
        {
          next: (result: Event) => {
            this.event = result;
            this.locationSeatGroupDTO.eventID = this.event.id;
            this.saveLocationAndSeatGroups(true);
          },
          error: (message: string) => {
            this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
              duration: 3000
            });
          }
        }
      );
    }
  }

  private getEventDays() {
    for (const ev of this.event.eventDays) {
      const from: Date = moment(ev.date, 'DD.MM.YYYY. HH:mm').toDate();
      const to: Date = moment(ev.date, 'DD.MM.YYYY. HH:mm').set({hour: 23, minute: 59, second: 59}).toDate();
      this.events.push(new AxiomSchedulerEvent('Event day', from, to, {id: ev.id}, colors[Math.floor(Math.random() * 15)]));
    }
    this.selectedEventDay = this.event.eventDays[0];
    this.refreshView();
  }

  private openDialog($event, create) {
    const dialogRef = this.dialog.open(DialogComponent);
    if (create) {
      dialogRef.componentInstance.createMode = true;
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const time = result.time.split(':');
          const from: Date = result.date.set({hour: time[0], minute: time[1]}).toDate();
          for (const evDay of this.events) {
            if (moment(evDay.from).isSame(moment(from), 'day')) {
              this.snackBar.open('Event day with the given date already exists', 'Dismiss', {
                duration: 3000
              });
              return;
            }
          }
          const to: Date = result.date.set({hour: 23, minute: 59, second: 59}).toDate();
          this.events.push(new AxiomSchedulerEvent('Event day', from, to, {id: null}, colors[Math.floor(Math.random() * 15)]));
          this.refreshView();
        }
      });
    } else {
      dialogRef.componentInstance.dateTime = {
        date: $event.from,
        time: moment($event.from.toString()).format('HH:mm')
      };
      dialogRef.componentInstance.createMode = false;
      dialogRef.afterClosed().subscribe(result => {
        if (result !== '' && result !== undefined) {
          const time = result.time.split(':');
          const from: Date = moment(result.date).set({hour: time[0], minute: time[1]}).toDate();
          const to: Date = moment(result.date).set({hour: 23, minute: 59, second: 59}).toDate();
          const index = this.events.indexOf($event);
          this.events[index] = new AxiomSchedulerEvent('Event day', from, to, $event.data, $event.color);
          this.refreshView();
        }
      });
    }
  }

  private removeEventDay($event) {
    const index = this.events.indexOf($event);
    if (index !== -1) {
      this.events.splice(index, 1);
      this.refreshView();
    }
  }

  private refreshView(): void {
    this.scheduler.refreshScheduler();
  }

  private getPicturesAndVideos() {
    this.eventApiService.getEventsPicturesAndVideos(this.event.id).subscribe(
      {
        next: (result: MediaFile[]) => {
          this.imageObject = [];
          const base64 = 'data:image/jpeg;base64,';
          for (const mediaFile of result) {
            let obj;
            if (mediaFile.fileType === 'image') {
              obj = {
                image: base64 + mediaFile.dataBase64, thumbImage: base64 + mediaFile.dataBase64,
                data: {id: mediaFile.id}
              };
            } else {
              obj = {video: base64 + mediaFile.dataBase64, data: {id: mediaFile.id}};
            }
            this.imageObject.push(obj);
          }
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  private deleteMediaFile() {
    const activeImage = this.slider.activeImageIndex;
    // @ts-ignore
    const id = this.imageObject[activeImage].data.id;
    this.eventApiService.deleteMediaFile(this.event.id, id).subscribe(
      {
        next: (result) => {
          this.imageObject.splice(activeImage, 1);
          this.slider.ligthboxShow = false;
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  private upload(item) {
    item.url = `http://localhost:8080/api/events/${this.event.id}/pictures-and-videos`;
    item.upload();
  }

  private uploadAll() {
    for (const file of this.uploader.queue) {
      this.upload(file);
    }
  }

  private getSeatGroups(id: number, newLoc: boolean) {
    this.locationApiService.getSeatGroups(id, 0, Number.MAX_SAFE_INTEGER).subscribe(
      {
        next: (result: Page) => {
          this.seatGroupComponent.seatGroups = result.content;
          if (newLoc) {
            this.locationSeatGroupDTO.eventSeatGroups = [];
            this.selectedSeatGroupId = -1;
            this.enabledSeatGroup = false;
            this.seatGroupComponent.redraw();
          }
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  private getLocationsOptions() {
    this.locationApiService.getLocationsOptions().subscribe({
      next: (result: Location[]) => {
        this.locationsOptions = result;
      },
      error: (message: string) => {
        this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
          duration: 3000
        });
      }
    });
  }

  private changeSelectedLocation($event) {
    if ($event.value == null) {
      this.locationSeatGroupDTO.eventSeatGroups = [];
      this.selectedSeatGroupId = -1;
      this.enabledSeatGroup = false;
      this.seatGroupComponent.seatGroups = [];
      return;
    }
    const newLoc = $event.value !== this.locationSeatGroupDTO.eventID;
    if (newLoc) {
      this.getSeatGroups($event.value, newLoc);
    } else {
      this.getEventLocationAndSeatGroups();
    }
  }

  private getEventLocationAndSeatGroups() {
    this.eventApiService.getEventLocationAndSeatGroups(this.event.id).subscribe(
      {
        next: (result: LocationSeatGroupDTO) => {
          this.locationSeatGroupDTO = result;
          if (this.locationSeatGroupDTO.locationID != null) {
            this.getSeatGroups(this.locationSeatGroupDTO.locationID, false);
          }
          this.selectedSeatGroupId = -1;
          this.enabledSeatGroup = false;
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  private seatGroupClicked($event) {
    this.selectedSeatGroupId = $event;
    this.enabledSeatGroup = false;
    const selectedGroup = this.locationSeatGroupDTO.eventSeatGroups.filter(esg => esg.seatGroupID === $event);
    if (selectedGroup.length !== 0) {
      this.selectedSeatGroupIndex = this.locationSeatGroupDTO.eventSeatGroups.indexOf(selectedGroup[0]);
      this.enabledSeatGroup = true;
    }
  }

  private seatGroupStatusChanged($event) {
    if ($event.checked) {
      const esgDTO: EventSeatGroupDTO = new EventSeatGroupDTO(this.selectedSeatGroupId, 1, []);
      this.locationSeatGroupDTO.eventSeatGroups.push(esgDTO.serialize());
      this.selectedSeatGroupIndex = this.locationSeatGroupDTO.eventSeatGroups.length - 1;
    } else {
      this.locationSeatGroupDTO.eventSeatGroups.splice(this.selectedSeatGroupIndex, 1);
    }

    this.seatGroupComponent.redraw();
  }

  private saveLocationAndSeatGroups(serializing: boolean) {
    const locationSeatGroup = serializing ? this.locationSeatGroupDTO.serialize() : this.locationSeatGroupDTO;
    this.eventApiService.setEventLocationAndSeatGroups(locationSeatGroup).subscribe(
      {
        next: (result: Event) => {
          this.snackBar.open('Event saved successfully', 'Dismiss', {
            duration: 3000
          });
          this.router.navigate(['/dashboard/events/', result.id]).then(r => {
          });
        },
        error: (message: string) => {
          this.snackBar.open(JSON.parse(JSON.stringify(message)).error, 'Dismiss', {
            duration: 3000
          });
          this.getEventLocationAndSeatGroups();
        }
      }
    );
  }

  private seatOrParterreClicked($event: { newTicketDetailed: NewTicketDetailed, mouseEvent: MouseEvent }) {
    if (this.reservation.tickets.length >= this.event.maxTicketsPerReservation) {
      return;
    }
    const {newTicketDetailed, mouseEvent} = $event;
    this.contextMenuPosition.x = mouseEvent.clientX + 'px';
    this.contextMenuPosition.y = mouseEvent.clientY + 'px';
    const reservableAllDays = newTicketDetailed.seatId ?
      this.seatAvailableAllDays(newTicketDetailed.rowNum, newTicketDetailed.colNum, newTicketDetailed.reservableSeatGroupId)
      : this.parterreAvailableAllDays(newTicketDetailed.reservableSeatGroupId);
    this.contextMenu.menuData = {
      newTicketDetailed,
      reservableAllDays
    };
    this.contextMenu.openMenu();
  }

  private makeTicket(newTicketDetailed: NewTicketDetailed, allDayTicket: boolean) {
    newTicketDetailed.allDayTicket = allDayTicket;
    if (allDayTicket) {
      newTicketDetailed.price = newTicketDetailed.price * this.event.eventDays.length;
    }
    this.reservation.tickets = this.reservation.tickets.concat([newTicketDetailed]);
  }

  private seatAvailableAllDays(rowNum: number, colNum: number, reservableSeatGroupId: number): boolean {
    const esg: EventSeatGroupDTO = this.locationSeatGroupDTO.eventSeatGroups
      .filter(es => es.reservableSeatGroups.map(rsg => rsg.id).includes(reservableSeatGroupId))[0];
    for (const rsg of esg.reservableSeatGroups) {
      const seat: SeatDTO = rsg.seats.filter(s => s.colNum === colNum && s.rowNum === rowNum)[0];
      if (seat.reserved) {
        return false;
      }
      if (this.reservation.tickets.map(t => t.seatId).includes(seat.id)) {
        return false;
      }
    }
    return true;
  }

  private parterreAvailableAllDays(reservableSeatGroupId: number): boolean {
    const esg: EventSeatGroupDTO = this.locationSeatGroupDTO.eventSeatGroups
      .filter(es => es.reservableSeatGroups.map(rsg => rsg.id).includes(reservableSeatGroupId))[0];
    for (const rsg of esg.reservableSeatGroups) {
      const reservingTicketsNum: number = this.reservation.tickets
        .filter(t => esg.reservableSeatGroups.map(rs => rs.id).includes(t.reservableSeatGroupId)
          && (t.allDayTicket || t.reservableSeatGroupId === rsg.id)).length;
      if (rsg.freeSeats - reservingTicketsNum <= 0) {
        return false;
      }
    }
    return true;
  }

  private onReservationChange() {
    this.getEventLocationAndSeatGroups();
    this.reservation = new NewReservation(this.event.id);
  }
}
