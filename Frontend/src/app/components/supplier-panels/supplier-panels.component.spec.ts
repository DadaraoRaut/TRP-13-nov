import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierPanelsComponent } from './supplier-panels.component';

describe('SupplierPanelsComponent', () => {
  let component: SupplierPanelsComponent;
  let fixture: ComponentFixture<SupplierPanelsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierPanelsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SupplierPanelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
