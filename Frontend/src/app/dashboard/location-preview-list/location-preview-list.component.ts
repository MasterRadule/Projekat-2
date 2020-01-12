import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import {Page} from '../../shared/model/page.model';
import {LocationApiService} from '../../core/location-api.service';
import {MatSnackBar, PageEvent} from '@angular/material';
import {Location} from '../../shared/model/location.model';

@Component({
  selector: 'app-location-preview-list',
  templateUrl: './location-preview-list.component.html',
  styleUrls: ['./location-preview-list.component.scss']
})
export class LocationPreviewListComponent implements OnInit {
  private _locations: Location[];
  private _page: Page;
  @Output() locationsPageChanged = new EventEmitter<Page>();

  constructor(private _locationApiService: LocationApiService, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    this.getLocations(0, 6);
  }

  public getLocations(page: number, size: number) {
    this._locationApiService.getLocations(page, size).subscribe({
      next: (result: Page) => {
        this._page = result;
        this.locationsPageChanged.emit(result);
        this._locations = result.content;
      },
      error: (message: string) => {
        this._snackBar.open(message);
      }
    });
  }

  public pageChanged(event: PageEvent) {
    this._page.size = event.pageSize;
    this._page.number = event.pageIndex;
    this.getLocations(this._page.number, this._page.size);
  }
}
