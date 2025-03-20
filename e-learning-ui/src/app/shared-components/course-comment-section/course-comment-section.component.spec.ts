import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseCommentSectionComponent } from './course-comment-section.component';

describe('CourseCommentSectionComponent', () => {
  let component: CourseCommentSectionComponent;
  let fixture: ComponentFixture<CourseCommentSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseCommentSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CourseCommentSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
