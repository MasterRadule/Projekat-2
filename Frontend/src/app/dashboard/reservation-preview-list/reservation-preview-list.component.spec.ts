import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationPreviewListComponent } from './reservation-preview-list.component';

describe('ReservationPreviewListComponent', () => {
  let component: ReservationPreviewListComponent;
  let fixture: ComponentFixture<ReservationPreviewListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReservationPreviewListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReservationPreviewListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
