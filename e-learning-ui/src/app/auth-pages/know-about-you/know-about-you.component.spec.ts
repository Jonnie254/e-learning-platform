import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KnowAboutYouComponent } from './know-about-you.component';

describe('KnowAboutYouComponent', () => {
  let component: KnowAboutYouComponent;
  let fixture: ComponentFixture<KnowAboutYouComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KnowAboutYouComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(KnowAboutYouComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
