import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SeatGroupsComponent } from './seat-groups.component';

describe('SeatGroupsComponent', () => {
  let component: SeatGroupsComponent;
  let fixture: ComponentFixture<SeatGroupsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SeatGroupsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SeatGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
