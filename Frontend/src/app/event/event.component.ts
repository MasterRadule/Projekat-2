import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {Event} from '../shared/model/event.model';
import {EventDay} from '../shared/model/event-day.model';
import {MediaFile} from '../shared/model/media-file.model';
import {ActivatedRoute, Router} from '@angular/router';
import {EventApiService} from '../core/event-api.service';
import {LocationApiService} from '../core/location-api.service';
import {MatSnackBar} from '@angular/material';
import {MatDialog} from '@angular/material/dialog';
import { AxiomSchedulerEvent, AxiomSchedulerComponent } from 'axiom-scheduler';
import {DialogComponent} from './dialog/dialog.component';
import { colors } from './colors';
import {NgImageSliderComponent} from 'ng-image-slider';
import { FileUploader } from 'ng2-file-upload';
import {Page} from '../shared/model/page.model';
import {SeatGroupsComponent} from '../seat-groups/seat-groups.component';
import {LocationSeatGroupDTO} from '../shared/model/location-seat-group-dto.model';
import {EventSeatGroupDTO} from '../shared/model/event-seat-group-dto.model';
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
  private imageObject: Array<object> = [];
  private uploader: FileUploader;
  private locationsOptions: Location[];
  private locationSeatGroupDTO: LocationSeatGroupDTO = new LocationSeatGroupDTO(null, null, []);
  private selectedSeatGroupIndex: number;
  private selectedSeatGroupId: number = -1;
  private enabledSeatGroup: boolean = false;
  @ViewChild(AxiomSchedulerComponent, {static: false}) scheduler: AxiomSchedulerComponent;
  @ViewChild('slider', {static: false}) slider: NgImageSliderComponent;
  @ViewChild(SeatGroupsComponent, {static: false}) seatGroupComponent: SeatGroupsComponent;

  constructor(private route: ActivatedRoute, private eventApiService: EventApiService, private locationApiService: LocationApiService,
    private snackBar: MatSnackBar, private router: Router, private dialog: MatDialog) {
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
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      });
  }

  private createOrEditEvent() {
    this.event.eventDays = [];
    for (let eventDay of this.events) {
      let date: string = moment(eventDay.from).format('DD.MM.YYYY. HH:mm');
      this.event.eventDays.push(new EventDay(eventDay.data.id, date, null).serialize());
    }
    if (this.event.id) {
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
    else {
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
    }
  }

  private getEventDays() {
    for (let ev of this.event.eventDays) {
       let from :Date = moment(ev.date, "DD.MM.YYYY. HH:mm").toDate();
       let to: Date = moment(ev.date, "DD.MM.YYYY. HH:mm").set({hour:23, minute:59, second:59}).toDate();
       this.events.push(new AxiomSchedulerEvent("Event day", from, to, {"id":ev.id}, colors[Math.floor(Math.random()*15)]));
    }
    this.refreshView();
  }

  private openDialog($event, create) {
    const dialogRef = this.dialog.open(DialogComponent);
    if (create) {
        dialogRef.componentInstance.createMode = true;
        dialogRef.afterClosed().subscribe(result => {
        if (result) {
          let time = result.time.split(":");
          let from: Date = result.date.set({hour:time[0], minute:time[1]}).toDate();
          for (let evDay of this.events) {
            if (moment(evDay.from).isSame(moment(from), 'day')) {
              this.snackBar.open("Event day with the given date already exists", 'Dismiss', {
                duration: 3000
              });
              return;
            }
          }
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

  private removeEventDay($event) {
    let index = this.events.indexOf($event);
    if (index != -1) {
      this.events.splice(index, 1);
      this.refreshView();
    }
  }

  private refreshView() : void {
    this.scheduler.refreshScheduler();
  }

  private getPicturesAndVideos() {
    this.eventApiService.getEventsPicturesAndVideos(this.event.id).subscribe(
        {
          next: (result: MediaFile[]) => {
            this.imageObject = [];
            var base64 = "data:image/jpeg;base64,";
            for (let mediaFile of result) {
            	let obj;
            	if (mediaFile.fileType === "image") {
            		obj = {"image": base64 + mediaFile.dataBase64, "thumbImage": base64 + mediaFile.dataBase64,
            				"data": {"id": mediaFile.id}};
            	}
            	else {
					obj = {"video": base64 + mediaFile.dataBase64, "data": {"id": mediaFile.id}};
            	}
            	this.imageObject.push(obj);
            }
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
              duration: 3000
            });
          }
        }
      );
  }

  private deleteMediaFile() {
  	let activeImage = this.slider.activeImageIndex;
  	let id = this.imageObject[activeImage]["data"]["id"];
  	this.eventApiService.deleteMediaFile(this.event.id, id).subscribe(
        {
          next: (result) => {
            this.imageObject.splice(activeImage, 1);
          	this.slider.ligthboxShow = false;
          },
          error: (message: string) => {
            this.snackBar.open(message, 'Dismiss', {
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
    for (let file of this.uploader.queue) {
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
            this.seatGroupComponent.enabledSeatGroupsIds = [];
            this.seatGroupComponent.redraw();
          } 
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
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
        this.snackBar.open(message);
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
    let newLoc = $event.value === this.locationSeatGroupDTO.eventID ? false : true;
    if (newLoc) {
      this.getSeatGroups($event.value, newLoc);
    }
    else {
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
            this.locationSeatGroupDTO.eventSeatGroups.forEach(esg => this.seatGroupComponent.enabledSeatGroupsIds.push(esg.seatGroupID));
          }
          this.selectedSeatGroupId = -1;
          this.enabledSeatGroup = false;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  private seatGroupClicked($event) {
    this.selectedSeatGroupId = $event;
    this.enabledSeatGroup = false;
    let selectedGroup = this.locationSeatGroupDTO.eventSeatGroups.filter(esg => esg.seatGroupID == $event);
    if (selectedGroup.length != 0) {
      this.selectedSeatGroupIndex = this.locationSeatGroupDTO.eventSeatGroups.indexOf(selectedGroup[0]);
      this.enabledSeatGroup = true;
    }
  }

  private seatGroupStatusChanged($event) {
    if ($event.checked) {
      let esgDTO: EventSeatGroupDTO = new EventSeatGroupDTO(this.selectedSeatGroupId, 1);
      this.locationSeatGroupDTO.eventSeatGroups.push(esgDTO.serialize());
      this.selectedSeatGroupIndex = this.locationSeatGroupDTO.eventSeatGroups.length - 1;
      this.seatGroupComponent.enabledSeatGroupsIds.push(this.selectedSeatGroupId);
    }
    else {
      this.locationSeatGroupDTO.eventSeatGroups.splice(this.selectedSeatGroupIndex, 1);
      let idx = this.seatGroupComponent.enabledSeatGroupsIds.indexOf(this.selectedSeatGroupId);
      this.seatGroupComponent.enabledSeatGroupsIds.splice(idx, 1);
    }

    this.seatGroupComponent.redraw();
  }

  private saveLocationAndSeatGroups() {
    if (this.locationSeatGroupDTO.eventSeatGroups.length == 0) {
      this.snackBar.open("At least one seat group must be enabled", 'Dismiss', {
        duration: 3000
      });
      return;
    }
    this.eventApiService.setEventLocationAndSeatGroups(this.locationSeatGroupDTO).subscribe(
      {
        next: (result: Event) => {
          this.snackBar.open("Location and seat groups successfully saved", 'Dismiss', {
            duration: 3000
          });
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
          this.getEventLocationAndSeatGroups();
        }
      }
    );
  }
}