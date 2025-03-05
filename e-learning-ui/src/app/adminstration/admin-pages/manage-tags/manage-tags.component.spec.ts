import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageTagsComponent } from './manage-tags.component';

describe('ManageTagsComponent', () => {
  let component: ManageTagsComponent;
  let fixture: ComponentFixture<ManageTagsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageTagsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
