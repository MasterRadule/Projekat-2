import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationPreviewListComponent } from './location-preview-list.component';

describe('LocationPreviewListComponent', () => {
  let component: LocationPreviewListComponent;
  let fixture: ComponentFixture<LocationPreviewListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LocationPreviewListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationPreviewListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
