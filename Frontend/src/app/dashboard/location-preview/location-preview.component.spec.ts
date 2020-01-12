import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LocationPreviewComponent} from './location-preview.component';
import {CoreModule} from '../../core/core.module';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {Location} from '../../shared/model/location.model';

describe('LocationPreviewComponent', () => {
  let component: LocationPreviewComponent;
  let fixture: ComponentFixture<LocationPreviewComponent>;
  const location: Location = new Location(1, 'Spens', 54.0, 58.0, false);

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        CoreModule,
        MatIconModule,
        MatSnackBarModule,
        MatSlideToggleModule,
        HttpClientTestingModule
      ],
      declarations: [LocationPreviewComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationPreviewComponent);
    component = fixture.componentInstance;
    component.setLocation = location;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
