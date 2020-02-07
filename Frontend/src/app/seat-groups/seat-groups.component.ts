import {Component, Input, OnDestroy, OnInit, Output, EventEmitter} from '@angular/core';
import {SeatGroup} from '../shared/model/seat-group.model';
import {EventDay} from '../shared/model/event-day.model';
import {LocationSeatGroupDTO} from '../shared/model/location-seat-group-dto.model';
import {EventSeatGroupDTO} from '../shared/model/event-seat-group-dto.model';
import {ReservableSeatGroupDTO} from '../shared/model/reservable-seat-group-dto.model';
import {SeatDTO} from '../shared/model/seat-dto.model';
import Konva from 'konva';

@Component({
  selector: 'app-seat-groups',
  templateUrl: './seat-groups.component.html',
  styleUrls: ['./seat-groups.component.scss']
})
export class SeatGroupsComponent implements OnInit, OnDestroy {
  private _seatGroups: SeatGroup[] = [];
  @Input() private width: number;
  @Input() private height: number;
  @Input() private mode: string;
  @Input() private enabledEventSeatGroups: LocationSeatGroupDTO;
  @Output() seatGroupClicked = new EventEmitter<number>();

  private stage: Konva.Stage;
  private layer: Konva.Layer;
  private seatGroupRepresentations: Konva.Group[] = [];
  private transformersMap = new Map<Konva.Group, Konva.Transformer>();
  private _selectedEventDayID: number;
  private selectedSeatGroupIndex: number;

  constructor() {
  }

  get selectedEventDayID(): number {
    return this._selectedEventDayID;
  }

  @Input('selectedEventDayID')
  set selectedEventDayID(selectedEventDayID: number) {
    if (selectedEventDayID) {
      this._selectedEventDayID = selectedEventDayID;
      this.layer.destroyChildren();
      this.setUpSeatGroups();
      this.layer.draw();
    }
  }

  private setUpSeatsOrParterre(seatGroup: SeatGroup, seatGroupRepresentation: Konva.Group) {
    if (seatGroup.parterre) {
      let freeSeats = null;
      if (['NO_ROLE_EVENT', 'ROLE_USER_EVENT'].includes(this.mode)) {
        freeSeats = this.getNumberOfFreeSeats(seatGroup.id);
      }
      const text = new Konva.Text({
        x: 0,
        y: 0,
        text: freeSeats ? freeSeats : seatGroup.name,
        fontSize: 18,
        fontFamily: 'Roboto',
        fill: '#c1c1c1',
        width: 200,
        padding: 20,
        align: 'center'
      });
      seatGroupRepresentation.add(text);
      seatGroupRepresentation.add(new Konva.Rect({
        x: 0,
        y: 0,
        stroke: this.mode.includes('LOCATION') ? '#c1c1c1':'black',
        strokeWidth: 1,
        width: text.width(),
        height: text.height(),
        shadowColor: 'black',
        shadowBlur: 10,
        shadowOffsetX: 10,
        shadowOffsetY: 10,
        shadowOpacity: 0.2,
        cornerRadius: 5
      }));
    } else {
      seatGroupRepresentation.add(new Konva.Text({
        x: 0,
        y: seatGroup.rowsNum * 35,
        text: seatGroup.name,
        fontSize: 18,
        fontFamily: 'Roboto',
        fill: '#c1c1c1',
        width: seatGroup.colsNum * 30,
        align: 'center',
        padding: 5,
      }));
      let reserved = null;
      for (let i = 0; i < seatGroup.colsNum; i++) {
        for (let j = 0; j < seatGroup.rowsNum; j++) {
          console.log(this.mode);
          if (['NO_ROLE_EVENT', 'ROLE_USER_EVENT'].includes(this.mode)) {
            reserved = this.checkIfSeatIsReserved(seatGroup.id, i + 1, j + 1);
          }
          seatGroupRepresentation.add(new Konva.Rect({
            x: i * 35,
            y: j * 35,
            width: 30,
            height: 30,
            fill: reserved ? '#FF0000':'#086275',
            stroke: 'black',
            strokeWidth: 1,
            cornerRadius: 5
          }));
        }
      }
    }
  }

  private static setUpRotationSnaps(seatGroup) {
    return new Konva.Transformer({
      node: seatGroup,
      centeredScaling: true,
      rotationSnaps: [0, 90, 180, 270],
      resizeEnabled: false
    });
  }

  ngOnInit(): void {
    this.setUpStage();
    this.layer = new Konva.Layer();
    this.stage.add(this.layer);
  }

  ngOnDestroy(): void {
    this.stage.destroy();
  }

  get seatGroups(): SeatGroup[] {
    return this._seatGroups;
  }

  set seatGroups(value: SeatGroup[]) {
    this._seatGroups = value;
    this.layer.destroyChildren();
    this.setUpSeatGroups();
    this.layer.draw();
  }

  private setUpStage() {
    this.stage = new Konva.Stage({
      container: 'konva',
      width: this.width,
      height: this.height,
      draggable: true
    });

    this.stage.on('dragstart', () => {
      this.stage.container().style.cursor = 'move';
    });

    this.stage.on('dragend', () => {
      this.stage.container().style.cursor = 'default';
    });

    this.stage.on('dblclick', (e) => {
      if (e.target.getType() === 'Stage') {
        this.transformersMap.forEach((value, key) => {
          value.detach();
          const seatGroup = this.seatGroups.find((sg) => {
            return sg.id.toString() === key.id();
          });
          seatGroup.angle = key.rotation();
          seatGroup.xCoordinate = key.position().x - this.stage.position().x;
          seatGroup.yCoordinate = key.position().y - this.stage.position().y;
          seatGroup.changed = true;
        });
        e.target.draw();
      }
    });
  }

  private setUpSeatGroups() {
    for (const seatGroup of this._seatGroups) {
      const seatGroupRepresentation = this.setUpSeatGroup(seatGroup);
      this.setUpSeatsOrParterre(seatGroup, seatGroupRepresentation);
      this.seatGroupRepresentations.push(seatGroupRepresentation);
      this.layer.add(seatGroupRepresentation);
    }
  }

  private setUpSeatGroup(seatGroup: SeatGroup): Konva.Group {
    const seatGroupRepresentation = new Konva.Group({
      x: this.stage.getPosition().x + seatGroup.xCoordinate,
      y: this.stage.getPosition().y + seatGroup.yCoordinate,
      rotation: seatGroup.angle,
      draggable: this.mode === 'ROLE_ADMIN_LOCATION',
      id: seatGroup.id.toString()
    });

    if (this.mode === 'ROLE_ADMIN_LOCATION') {
      seatGroupRepresentation.on('dragstart', () => {
        this.stage.container().style.cursor = 'pointer';
      });

      seatGroupRepresentation.on('dragend', () => {
        this.stage.container().style.cursor = 'default';
        seatGroup.angle = seatGroupRepresentation.rotation();
        seatGroup.xCoordinate = seatGroupRepresentation.getPosition().x - this.stage.getPosition().x;
        seatGroup.yCoordinate = seatGroupRepresentation.getPosition().y - this.stage.getPosition().y;
        seatGroup.changed = true;
      });

      seatGroupRepresentation.on('dblclick', () => {
        if (!this.transformersMap.has(seatGroupRepresentation)) {
          const rotationSnap = SeatGroupsComponent.setUpRotationSnaps(seatGroupRepresentation);
          this.transformersMap.set(seatGroupRepresentation, rotationSnap);
          this.layer.add(rotationSnap);
        } else {
          this.transformersMap.get(seatGroupRepresentation).attachTo(seatGroupRepresentation);
        }
        this.layer.draw();
      });
    }
    else {
      seatGroupRepresentation.on('click', () => {
        this.redraw();
        this.changeStroke(seatGroupRepresentation, "red", 2);
        this.selectedSeatGroupIndex = seatGroupRepresentation._id;
        this.layer.draw();
        this.seatGroupClicked.emit(parseInt(seatGroupRepresentation.id()));
      });
    }
    
    return seatGroupRepresentation;
  }

  addSeatGroup(seatGroup: SeatGroup) {
    this.seatGroups.push(seatGroup);
    const seatGroupRepresentation = this.setUpSeatGroup(seatGroup);
    this.setUpSeatsOrParterre(seatGroup, seatGroupRepresentation);
    this.seatGroupRepresentations.push(seatGroupRepresentation);
    this.layer.add(seatGroupRepresentation);
    seatGroupRepresentation.draw();
  }

  changeStroke(seatGroupRepresentation: Konva.Group | Konva.Shape, color: String, strokeWidth: number) {
    seatGroupRepresentation.children.each((child, index) => {
      if (index != 0) {
        child.setAttr("stroke", color);
        child.setAttr("strokeWidth", strokeWidth);
      }
    });
  }

  redraw() {
    this.layer.children.each((child, index) => {
      const enabledSeatGroupIds = this.enabledEventSeatGroups.eventSeatGroups.map(esg => esg.seatGroupID);
      if (enabledSeatGroupIds.includes(parseInt(child.id()))) {
        this.changeStroke(child, "black", 1);
      }
      else {
        this.changeStroke(child, "#c1c1c1", 2);
      }
    });
    this.layer.draw();
  }

  checkIfSeatIsReserved(seatGroupID: number, rowNum: number, colNum: number) {
    let eventSeatGroup : EventSeatGroupDTO;
    for (let esg of this.enabledEventSeatGroups.eventSeatGroups) {
      if (esg.seatGroupID === seatGroupID) {
        eventSeatGroup = esg;
        break;
      }
    }

    let reservableSeatGroup: ReservableSeatGroupDTO;
    for (let rsg of eventSeatGroup.reservableSeatGroups) {
      if (rsg.eventDayID === this.selectedEventDayID) {
        reservableSeatGroup = rsg;
        break;
      }
    }

    let seat: SeatDTO;
    for (let s of reservableSeatGroup.seats) {
      if (s.rowNum === rowNum && s.colNum === colNum) {
        seat = s;
        break;
      }
    }

    return seat.reserved;
  }

  getNumberOfFreeSeats(seatGroupID: number) {
    let eventSeatGroup : EventSeatGroupDTO;
    for (let esg of this.enabledEventSeatGroups.eventSeatGroups) {
      if (esg.seatGroupID === seatGroupID) {
        eventSeatGroup = esg;
        break;
      }
    }

    let reservableSeatGroup: ReservableSeatGroupDTO;
    for (let rsg of eventSeatGroup.reservableSeatGroups) {
      if (rsg.eventDayID === this.selectedEventDayID) {
        reservableSeatGroup = rsg;
        break;
      }
    }

    return reservableSeatGroup.freeSeats.toString();
  }
}
