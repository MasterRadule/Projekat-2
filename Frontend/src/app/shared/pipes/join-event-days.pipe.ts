import {Pipe, PipeTransform} from '@angular/core';
import {formatDate} from '@angular/common';

@Pipe({
  name: 'joinEventDays'
})
export class JoinEventDaysPipe implements PipeTransform {

  transform(value: Date[], ...args: any[]): any {
    return value.map(date => formatDate(date, 'dd/MM/yyyy', 'en-US')).join(', ');
  }

}
