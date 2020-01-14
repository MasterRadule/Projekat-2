import {Component, OnInit, Output, EventEmitter} from '@angular/core';
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
  @Output() resetPaginator = new EventEmitter<any>();
  private _searchParameter = '';

  constructor(private _locationApiService: LocationApiService, private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    this.getLocations(0, 6);
  }

  get locations(): Location[] {
    return this._locations;
  }

  public getLocations(page: number, size: number) {
    this._locationApiService.getLocations(page, size).subscribe({
      next: (result: Page) => {
        this._page = result;
        this.locationsPageChanged.emit(result);
        this._locations = result.content;
      },
      error: (message: string) => {
        this.snackBar.open(message, 'Dismiss', {
          duration: 3000
        });
      }
    });
  }

  public pageChanged(event: PageEvent) {
    this._page.size = event.pageSize;
    this._page.number = event.pageIndex;
    this.searchLocations(this._page.number, this._page.size);
  }

  private searchLocations(page: number, size: number) {
    this._locationApiService.searchLocations(this._searchParameter, page, size).subscribe(
      {
        next: (result: Page) => {
          this._page = result;
          this.locationsPageChanged.emit(result);
          this._locations = result.content;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  private onSubmit() {
    this.searchLocations(this._page.number, this._page.size);
  }

  private resetForm(form) {
    form.reset();
    this._searchParameter = '';
    this.resetPaginator.emit();
    this.getLocations(0, 6);
  }
}
