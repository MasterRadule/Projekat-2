import {Component, Input, OnDestroy, OnInit, Output, EventEmitter} from '@angular/core';
import {SeatGroup} from '../shared/model/seat-group.model';
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
  @Input() private draggable: boolean;
  @Output() seatGroupClicked = new EventEmitter<number>();

  private stage: Konva.Stage;
  private layer: Konva.Layer;
  private seatGroupRepresentations: Konva.Group[] = [];
  private transformersMap = new Map<Konva.Group, Konva.Transformer>();
  private selectedSeatGroupIndex: number;
  public enabledSeatGroupsIds: number[] = [];

  constructor() {
  }

  private setUpSeatsOrParterre(seatGroup: SeatGroup, seatGroupRepresentation: Konva.Group) {
    if (seatGroup.parterre) {
      const text = new Konva.Text({
        x: 0,
        y: 0,
        text: seatGroup.name,
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
        stroke: this.draggable ? '#c1c1c1' : 'black',
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
      for (let i = 0; i < seatGroup.colsNum; i++) {
        for (let j = 0; j < seatGroup.rowsNum; j++) {
          seatGroupRepresentation.add(new Konva.Rect({
            x: i * 35,
            y: j * 35,
            width: 30,
            height: 30,
            fill: '#086275',
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
      draggable: this.draggable,
      id: seatGroup.id.toString()
    });

    if (this.draggable) {
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
    } else {
      seatGroupRepresentation.on('click', () => {
        this.redraw();
        this.changeStroke(seatGroupRepresentation, 'red', 2);
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
        child.setAttr('stroke', color);
        child.setAttr('strokeWidth', strokeWidth);
      }
    });
  }

  redraw() {
    this.layer.children.each((child, index) => {
      if (this.enabledSeatGroupsIds.includes(parseInt(child.id()))) {
        this.changeStroke(child, 'black', 1);
      } else {
        this.changeStroke(child, '#c1c1c1', 2);
      }
    });
    this.layer.draw();
  }
}
