import {Component, Input, OnInit} from '@angular/core';
import {Observable, of} from 'rxjs';
import {SeatGroup} from '../shared/model/seat-group.model';

@Component({
  selector: 'app-seat-groups',
  templateUrl: './seat-groups.component.html',
  styleUrls: ['./seat-groups.component.scss']
})
export class SeatGroupsComponent implements OnInit {
  @Input() private seatGroups: SeatGroup[];
  @Input() private width: number;
  @Input() private height: number;

  constructor() {
  }

  private makeStage(width: number, height: number): Observable<any> {
    return of({
      width,
      height
    });
  }

  private drawSeatGroup(seatGroup: SeatGroup): Observable<any> {
    return of({
      x: seatGroup.xCoordinate,
      y: seatGroup.yCoordinate,
      width: 100 * seatGroup.colsNum,
      height: 50 * seatGroup.rowsNum,
      fill: 'green',
      stroke: 'black',
      strokeWidth: 4,
      opacity: 0.5
    });
  }

  private prepareSeats(seatGroup: SeatGroup) {
    const seats = [];

    for (let i = 0; i < seatGroup.rowsNum; i = i + 10) {
      for (let j = 0; j < seatGroup.colsNum; j = j + 10) {
        seats.push({
          x: i + seatGroup.xCoordinate,
          y: j + seatGroup.yCoordinate
        });
      }
    }

    return seats;
  }


  private drawSeat(seat: any): Observable<any> {
    return of({
      x: seat.x,
      y: seat.y,
      width: 50,
      height: 50,
      fill: 'red',
      stroke: 'black',
      strokeWidth: 2
    });
  }

  ngOnInit() {
  }


}
