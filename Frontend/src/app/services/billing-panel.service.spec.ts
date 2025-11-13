import { TestBed } from '@angular/core/testing';

import { BillingPanelService } from './billing-panel.service';

describe('BillingPanelService', () => {
  let service: BillingPanelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BillingPanelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
