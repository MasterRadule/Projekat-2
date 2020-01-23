import {Component, Input, OnInit} from '@angular/core';
import {SeatGroup} from '../shared/model/seat-group.model';
import Konva from 'konva';


@Component({
  selector: 'app-seat-groups',
  templateUrl: './seat-groups.component.html',
  styleUrls: ['./seat-groups.component.scss']
})
export class SeatGroupsComponent implements OnInit {
  @Input() private seatGroups: SeatGroup[];
  @Input() private width: number;
  @Input() private height: number;

  private stage: Konva.Stage;
  private layer: Konva.Layer;
  private seatGroupRepresentations: Konva.Group[] = [];
  private transformersMap = new Map<Konva.Group, Konva.Transformer>();

  constructor() {
  }

  ngOnInit(): void {
    this.setUpStage();
    this.layer = new Konva.Layer();
    this.setUpSeatGroups();
    this.stage.add(this.layer);
  }

  setUpStage() {
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
        this.transformersMap.forEach((value) => {
          value.detach();
        });
        e.target.draw();
      }
    });
  }

  setUpSeatGroups() {
    for (const seatGroup of this.seatGroups) {
      const seatGroupRepresentation = this.setUpSeatGroup(seatGroup);
      this.setUpSeatsOrParterre(seatGroup, seatGroupRepresentation);
      this.seatGroupRepresentations.push(seatGroupRepresentation);
      this.layer.add(seatGroupRepresentation);
    }
  }

  setUpRotationSnaps(seatGroup) {
    return new Konva.Transformer({
      node: seatGroup,
      centeredScaling: true,
      rotationSnaps: [0, 90, 180, 270],
      resizeEnabled: false
    });
  }

  setUpSeatGroup(seatGroup: SeatGroup): Konva.Group {
    const seatGroupRepresentation = new Konva.Group({
      x: this.stage.getPosition().x + seatGroup.xCoordinate,
      y: this.stage.getPosition().y + seatGroup.yCoordinate,
      rotation: seatGroup.angle,
      draggable: true,
      id: seatGroup.id.toString()
    });

    seatGroupRepresentation.on('dragstart', () => {
      this.stage.container().style.cursor = 'pointer';
    });

    seatGroupRepresentation.on('dragend', () => {
      this.stage.container().style.cursor = 'default';
    });

    seatGroupRepresentation.on('dblclick', () => {
      if (!this.transformersMap.has(seatGroupRepresentation)) {
        const rotationSnap = this.setUpRotationSnaps(seatGroupRepresentation);
        this.transformersMap.set(seatGroupRepresentation, rotationSnap);
        this.layer.add(rotationSnap);
      } else {
        this.transformersMap.get(seatGroupRepresentation).attachTo(seatGroupRepresentation);
      }
      this.layer.draw();
    });

    seatGroupRepresentation.on('dragmove', (e) => {
      const oldPos = e.target.getPosition();
      const movementX = e.evt.movementX;
      const movementY = e.evt.movementY;
      const potentialNewPosition = {x: oldPos.x + movementX, y: oldPos.y + movementY};

      const sg = e.target;
      sg.setPosition(potentialNewPosition);

      if (this.detectCollision(seatGroup)) {
        sg.setPosition(oldPos);
      }

      sg.draw();
    });

    return seatGroupRepresentation;
  }

  setUpSeatsOrParterre(seatGroup: SeatGroup, seatGroupRepresentation: Konva.Group) {
    if (seatGroup.parterre) {
      seatGroupRepresentation.add(new Konva.Rect({
        x: seatGroupRepresentation.getPosition().x,
        y: seatGroupRepresentation.getPosition().y,
        width: 100,
        height: 50,
        stroke: 'black',
        strokeWidth: 1
      }));
    } else {
      for (let i = 0; i < seatGroup.rowsNum; i++) {
        for (let j = 0; j < seatGroup.colsNum; j++) {
          seatGroupRepresentation.add(new Konva.Rect({
            x: i * 35,
            y: j * 35,
            width: 30,
            height: 30,
            fill: 'red',
            stroke: 'black',
            strokeWidth: 1
          }));
        }
      }
    }
  }

  detectCollision(seatGroup): boolean {
    const baseRect = this.createRectForCollision(seatGroup);

    this.seatGroupRepresentations.forEach((sg) => {
      if (sg.id() !== seatGroup.id()) {
        if (SAT.testPolygonPolygon(baseRect, this.createRectForCollision(sg))) {
          return true;
        }
      }
    });
    return false;
  }

  createRectForCollision(seatGroup: Konva.Group): SAT.POLYGON {
    return new SAT.POLYGON(new SAT.VECTOR(seatGroup.x, seatGroup.y), [
      new SAT.VECTOR(seatGroup.getAttr('x'), seatGroup.getAttr('y')),
      new SAT.VECTOR(seatGroup.getAttr('x') + seatGroup.getAttr('width'), seatGroup.getAttr('y')),
      new SAT.VECTOR(seatGroup.getAttr('x'), seatGroup.getAttr('y') + seatGroup.getAttr('height')),
      new SAT.VECTOR(seatGroup.getAttr('x') + seatGroup.getAttr('width'), seatGroup.getAttr('y') + seatGroup.getAttr('height'))
    ]);
  }
}
