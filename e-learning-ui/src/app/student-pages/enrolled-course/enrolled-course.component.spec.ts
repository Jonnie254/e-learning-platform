import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnrolledCourseComponent } from './enrolled-course.component';

describe('EnrolledCourseComponent', () => {
  let component: EnrolledCourseComponent;
  let fixture: ComponentFixture<EnrolledCourseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnrolledCourseComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnrolledCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
