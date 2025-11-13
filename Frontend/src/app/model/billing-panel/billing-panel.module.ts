import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { InvoiceComponent } from '../../components/billing-dashboard/invoice/invoice.component';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,

    InvoiceComponent

  ],
  exports: [
    InvoiceComponent]
})
export class BillingPanelModule {

}
