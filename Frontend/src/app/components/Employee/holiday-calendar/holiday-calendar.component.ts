import { CommonModule, Location } from '@angular/common'; // Import Location
import { HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-holiday-calendar',
  standalone: true,
  imports: [CommonModule, RouterOutlet,RouterModule,HttpClientModule],
  templateUrl: './holiday-calendar.component.html',
  styleUrls: ['./holiday-calendar.component.css']
})
export class HolidayCalendarComponent {

  holidays = [
    { date: '26 Jan 2026', name: 'Republic Day' },
    { date: '10 Mar 2026', name: 'Maha Shivratri' },
    { date: '18 Apr 2026', name: 'Good Friday' },
    { date: '01 May 2026', name: 'Maharashtra Day' },
  ];

  constructor(private location: Location) {} // Inject Location service

  // Method to go back
  goBack(): void {
    this.location.back();
  }
}
