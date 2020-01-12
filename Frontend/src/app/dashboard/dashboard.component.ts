import {Component, Input, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { EventPreviewListComponent} from './event-preview-list/event-preview-list.component';
import { LocationPreviewListComponent } from './location-preview-list/location-preview-list.component';
import { PaginatorComponent } from './paginator/paginator.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit {
  @ViewChild(PaginatorComponent, {static: false}) paginator: PaginatorComponent;
  @ViewChild(EventPreviewListComponent, {static: false}) eventListComponent: EventPreviewListComponent;
  @ViewChild(LocationPreviewListComponent, {static: false}) locationListComponent: LocationPreviewListComponent;
  private _content: string;

  constructor(private route: ActivatedRoute) {
  }

  get content(): string {
    return this._content;
  }

  set content(value: string) {
    this._content = value;
  }

  ngAfterViewInit() {}
  
  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this._content = params.get('content');
      if (this.paginator !== undefined) {
        this.resetPaginator();
      }
    });
  }

  private pageChanged($event) {
    if (this._content === 'locations') {
      this.locationListComponent.pageChanged($event);
    }
    else {
      this.eventListComponent.pageChanged($event);
    }
  }

  private contentPageChanged($event) {
    this.paginator.page = $event;
  }

  private resetPaginator() {
    this.paginator.matPaginator.firstPage();
  }
}