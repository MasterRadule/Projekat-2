import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {
  public dateTime = {date:"", time:""};
  public createMode: boolean = true;

  constructor() { }

  ngOnInit() {
  }

}
