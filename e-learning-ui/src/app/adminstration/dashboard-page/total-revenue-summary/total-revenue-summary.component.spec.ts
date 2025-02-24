import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TotalRevenueSummaryComponent } from './total-revenue-summary.component';

describe('TotalRevenueSummaryComponent', () => {
  let component: TotalRevenueSummaryComponent;
  let fixture: ComponentFixture<TotalRevenueSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TotalRevenueSummaryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TotalRevenueSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
