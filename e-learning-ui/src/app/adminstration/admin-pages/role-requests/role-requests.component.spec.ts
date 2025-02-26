import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleRequestsComponent } from './role-requests.component';

describe('RoleRequestsComponent', () => {
  let component: RoleRequestsComponent;
  let fixture: ComponentFixture<RoleRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleRequestsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoleRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
