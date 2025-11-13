import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-add-biller',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ReactiveFormsModule,HttpClientModule,RouterModule],
  templateUrl: './add-biller.component.html',
  styleUrls: ['./add-biller.component.css']
})
export class AddBillerComponent implements OnInit {
  billerForm!: FormGroup;

  // Static employee list instead of loading from service
  employees = [
    { id: 1, name: 'Alice Johnson' },
    { id: 2, name: 'Bob Smith' },
    { id: 3, name: 'Charlie Lee' }
  ];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.billerForm = this.fb.group({
      employeeId: ['', Validators.required],
      billerName: ['', Validators.required],
      billerEmail: ['', [Validators.required, Validators.email]],
      billerContact: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]]
    });
  }
  onClose(): void {
  // Handle close logic, e.g., navigate away or hide the form
}


  onSubmit(): void {
    if (this.billerForm.valid) {
      console.log('✅ Form submitted:', this.billerForm.value);
    } else {
      console.log('⚠️ Invalid form');
    }
  }
}
