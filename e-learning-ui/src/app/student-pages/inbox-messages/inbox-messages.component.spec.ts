import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InboxMessagesComponent } from './inbox-messages.component';

describe('InboxMessagesComponent', () => {
  let component: InboxMessagesComponent;
  let fixture: ComponentFixture<InboxMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InboxMessagesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InboxMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
