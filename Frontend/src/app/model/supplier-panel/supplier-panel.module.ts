import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// ✅ Correct relative imports
import { SupplierListComponent } from '../../components/supplier-panel/supplier-list/supplier-list.component';
import { PurchaseOrderComponent } from '../../components/supplier-panel/purchase-order/purchase-order.component';
import { OrderListComponent } from '../../components/supplier-panel/order-list/order-list.component';
import { SupplierItemFormComponent } from '../../components/supplier-panel/supplier-item-form/supplier-item-form.component';


@NgModule({
  imports: [
    CommonModule,
     RouterModule,
    // 👇 since these are standalone components
    SupplierListComponent,
    PurchaseOrderComponent,
    OrderListComponent,
    SupplierItemFormComponent
  ],
  exports: [
    // 👇 export them so that router or other modules can use them
    SupplierListComponent,
    PurchaseOrderComponent,
    OrderListComponent,
    SupplierItemFormComponent
  ]
})
export class SupplierPanelModule {}
