import { TestBed } from '@angular/core/testing';

import { KnowYouService } from './know-you.service';

describe('KnowYouService', () => {
  let service: KnowYouService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KnowYouService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
