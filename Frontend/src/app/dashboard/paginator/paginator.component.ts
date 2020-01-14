import {Component, OnInit, Output, EventEmitter, ViewChild} from '@angular/core';
import {Page} from '../../shared/model/page.model';
import {PageEvent, MatPaginator} from '@angular/material';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent implements OnInit {
  private _page: Page;
  @Output() pageChangedEvent = new EventEmitter<PageEvent>();
  @ViewChild(MatPaginator, {static: false}) matPaginator: MatPaginator;

  constructor() {
  }

  ngOnInit() {
    this._page = new Page();
  }

  get page(): Page {
    return this._page;
  }

  set page(value: Page) {
    this._page = value;
  }

  private pageChanged(event: PageEvent) {
    this.pageChangedEvent.emit(event);
  }

}
