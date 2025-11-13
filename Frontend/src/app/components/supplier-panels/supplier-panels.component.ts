import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-supplier-panels',
  standalone: true,
  imports: [CommonModule,RouterModule,RouterOutlet,HttpClientModule],
  templateUrl: './supplier-panels.component.html',
  styleUrl: './supplier-panels.component.css'
})
export class SupplierPanelsComponent {

}
