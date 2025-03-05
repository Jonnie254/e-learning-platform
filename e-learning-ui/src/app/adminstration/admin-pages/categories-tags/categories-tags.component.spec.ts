import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoriesTagsComponent } from './categories-tags.component';

describe('CategoriesTagsComponent', () => {
  let component: CategoriesTagsComponent;
  let fixture: ComponentFixture<CategoriesTagsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoriesTagsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoriesTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
