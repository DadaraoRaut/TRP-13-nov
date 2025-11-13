export interface AttendanceRecord {
  employeeId: string;
  date?: string;
  firstIn?: string;
  lastOut?: string;
  signInTime?: string;
  signOutTime?: string;
  status?: string;
  totalWorkHrs?: string;
  breakHrs?: string;
  actualWorkHrs?: string;
  shortfallHrs?: string;
  excessHrs?: string;
  lateIn?: string;
  earlyOut?: string;
  workHrs?: string;
  shift?: string;
  session1Start?: string;
  session1End?: string;
  session2Start?: string;
  session2End?: string;
  shiftStart?: string;
  shiftEnd?: string;
}

export interface Holiday {
  date: string;
  name: string;
}
