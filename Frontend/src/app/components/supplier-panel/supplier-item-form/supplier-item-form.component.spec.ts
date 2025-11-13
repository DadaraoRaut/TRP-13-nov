import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierItemFormComponent } from './supplier-item-form.component';

describe('SupplierItemFormComponent', () => {
  let component: SupplierItemFormComponent;
  let fixture: ComponentFixture<SupplierItemFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupplierItemFormComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SupplierItemFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
