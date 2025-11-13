import { TestBed } from '@angular/core/testing';

import { SupplierPanelService } from './supplier-panel.service';

describe('SupplierPanelService', () => {
  let service: SupplierPanelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SupplierPanelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
