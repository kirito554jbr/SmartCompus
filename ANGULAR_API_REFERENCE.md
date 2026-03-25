# SmartCampus — Angular Frontend API Reference

> **Base URL:** `http://localhost:8080`  
> **Auth:** All protected routes require `Authorization: Bearer <accessToken>` header.  
> **Content-Type:** `application/json`

---

## Table of Contents
1. [Authentication](#1-authentication)
2. [Users](#2-users)
3. [Students](#3-students)
4. [Teachers](#4-teachers)
5. [Courses](#5-courses)
6. [Timetables](#6-timetables)
7. [Rooms](#7-rooms)
8. [Absences](#8-absences)
9. [Requests](#9-requests)
10. [Enums Reference](#10-enums-reference)
11. [Role-Based Access Control](#11-role-based-access-control)
12. [Angular Service Examples](#12-angular-service-examples)

---

## 1. Authentication

**Base path:** `/api/auth`  
**All routes are public (no token required)**

---

### POST `/api/auth/register`
Register a new user. Send role-specific fields based on the `role` value.

**Request Body (Admin / Admin Staff):**
```json
{
  "firstName": "Alice",
  "lastName": "Admin",
  "email": "alice@campus.com",
  "password": "secret123",
  "role": "ROLE_ADMIN"
}
```

**Request Body (Student) — include extra student fields:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@student.com",
  "password": "secret123",
  "role": "ROLE_STUDENT",
  "studentNumber": "STU001",
  "major": "Computer Science"
}
```

**Request Body (Teacher) — include extra teacher fields:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@teacher.com",
  "password": "secret123",
  "role": "ROLE_TEACHER",
  "employeeNumber": "EMP001",
  "speciality": "Mathematics"
}
```

**Response `201 Created`:**
```json
{
  "idUser": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@student.com",
  "role": "ROLE_STUDENT"
}
```

---

### POST `/api/auth/login`
Login and receive JWT tokens.

**Request Body:**
```json
{
  "email": "john@student.com",
  "password": "secret123"
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@student.com",
  "role": "ROLE_STUDENT"
}
```

> **Store `accessToken` and `refreshToken` in localStorage/sessionStorage.**  
> Access token expires in **15 minutes**. Refresh token in **7 days**.

---

### POST `/api/auth/refresh-token`
Get a new access token using the refresh token.

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response `200 OK`:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "john@student.com",
  "role": "ROLE_STUDENT"
}
```

**Response `401 Unauthorized`** — if refresh token is invalid or expired.

---

## 2. Users

**Base path:** `/api/users`  
**Required roles:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`

---

### GET `/api/users/{id}`
Get a user by ID.

**Response `200 OK`:**
```json
{
  "idUser": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@student.com",
  "role": "ROLE_STUDENT"
}
```

---

### GET `/api/users/email?email={email}`
Get a user by email address.

**Query Param:** `email=john@student.com`

**Response `200 OK`:** Same as above.

---

### GET `/api/users/role/{role}`
Get all users with a specific role.

**Path values:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`, `ROLE_TEACHER`, `ROLE_STUDENT`

**Response `200 OK`:**
```json
[
  {
    "idUser": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@student.com",
    "role": "ROLE_STUDENT"
  }
]
```

---

### PUT `/api/users/{id}`
Update a user's profile.

**Request Body:** Same structure as register (role-specific).

**Response `200 OK`:** Updated `UserResponseDto`.

---

### PATCH `/api/users/{id}/password`
Change a user's password.

**Request Body:** Plain string (raw password)
```
"newPassword123"
```

**Response `200 OK`:** Empty body.

---

### DELETE `/api/users/{id}`
Delete a user by ID.

**Response `204 No Content`**

---

## 3. Students

**Base path:** `/api/students`  
**Required roles:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`, `ROLE_TEACHER`

---

### GET `/api/students/number/{studentNumber}`
Find a student by their student number.

**Response `200 OK`:**
```json
{
  "idUser": 2,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@student.com",
  "role": "ROLE_STUDENT",
  "studentNumber": "STU001",
  "major": "Computer Science"
}
```

---

### GET `/api/students/major/{major}`
Get all students in a specific major.

**Response `200 OK`:** Array of student objects (same as above).

---

## 4. Teachers

**Base path:** `/api/teachers`  
**Required roles:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`

---

### GET `/api/teachers/employee/{employeeNumber}`
Find a teacher by employee number.

**Response `200 OK`:**
```json
{
  "idUser": 3,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@teacher.com",
  "role": "ROLE_TEACHER",
  "employeeNumber": "EMP001",
  "speciality": "Mathematics"
}
```

---

### GET `/api/teachers/speciality/{speciality}`
Get all teachers by speciality.

**Response `200 OK`:** Array of teacher objects (same as above).

---

## 5. Courses

**Base path:** `/api/courses`  
**Required roles:** `ROLE_ADMIN`, `ROLE_TEACHER`

---

### POST `/api/courses`
Create a new course.

**Request Body:**
```json
{
  "name": "Calculus",
  "code": "MATH101"
}
```

**Response `201 Created`:**
```json
{
  "id": 10,
  "name": "Calculus",
  "code": "MATH101",
  "teacherId": null,
  "teacherFullName": null
}
```

---

### GET `/api/courses`
Get all courses.

**Response `200 OK`:**
```json
[
  {
    "id": 10,
    "name": "Calculus",
    "code": "MATH101",
    "teacherId": 3,
    "teacherFullName": "Jane Smith"
  }
]
```

---

### GET `/api/courses/{id}`
Get a course by ID.

**Response `200 OK`:** Single course object.

---

### GET `/api/courses/teacher/{teacherId}`
Get all courses taught by a specific teacher.

**Response `200 OK`:** Array of course objects.

---

### PATCH `/api/courses/{courseId}/assign-teacher/{teacherId}`
Assign a teacher to a course.

**Response `200 OK`:** Updated course object with `teacherId` and `teacherFullName` set.

---

### PATCH `/api/courses/{courseId}/enroll-student/{studentId}`
Enroll a student in a course.

**Response `200 OK`:**
```json
"Student enrolled successfully (studentId=2, courseId=10)"
```

**Response `500`** if student is already enrolled.

---

### DELETE `/api/courses/{id}`
Delete a course.

**Response `204 No Content`**

---

## 6. Timetables

**Base path:** `/api/timetables`  
**Required roles:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`

---

### POST `/api/timetables`
Create a new timetable entry (schedule a course in a room).

**Request Body:**
```json
{
  "date": "2026-05-23",
  "day": "Monday",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "courseId": 10,
  "roomId": 5
}
```

> `date` format: `yyyy-MM-dd`
> `day` values: `"Monday"`, `"Tuesday"`, `"Wednesday"`, `"Thursday"`, `"Friday"`, `"Saturday"`, `"Sunday"`  
> `startTime` / `endTime` format: `"HH:mm:ss"`

**Response `201 Created`:**
```json
{
  "id": 1,
  "date": "2026-05-23",
  "day": "Monday",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "courseId": 10,
  "courseName": "Calculus",
  "roomId": 5,
  "roomName": "Lab 101"
}
```

**Response `409 Conflict`** — if the room is already booked at that time.  
**Response `404 Not Found`** — if course or room doesn't exist.

---

### GET `/api/timetables/{id}`
Get a timetable entry by ID.

**Response `200 OK`:** Single timetable object.

---

### GET `/api/timetables/student/{studentId}`
Get the full schedule for a student (all courses they are enrolled in).

**Response `200 OK`:** Array of timetable objects.

---

### GET `/api/timetables/teacher/{teacherId}`
Get the full schedule for a teacher.

**Response `200 OK`:** Array of timetable objects.

---

### GET `/api/timetables/room-availability?roomId={id}&date={yyyy-MM-dd}&day={day}&startTime={HH:mm:ss}&endTime={HH:mm:ss}`
Check if a room is available at a specific time slot.

**Query Params:**
- `roomId=5`
- `date=2026-05-23`
- `day=Monday`
- `startTime=09:00:00`
- `endTime=11:00:00`

**Response `200 OK`:**
```json
true
```
or
```json
false
```

---

### DELETE `/api/timetables/{id}`
Delete a timetable entry.

**Response `204 No Content`**

---

## 7. Rooms

**Base path:** `/api/rooms`  
**Required roles:** `ROLE_ADMIN`, `ROLE_ADMIN_STAFF`

---

### POST `/api/rooms`
Create a new room.

**Request Body:**
```json
{
  "name": "Lab 101",
  "capacity": 30,
  "type": "PRACTICAL"
}
```

> `type` values: `"LECTURE"`, `"PRACTICAL"`, `"EXAM"`

**Response `201 Created`:**
```json
{
  "id": 5,
  "name": "Lab 101",
  "capacity": 30,
  "type": "PRACTICAL"
}
```

---

### GET `/api/rooms`
Get all rooms.

**Response `200 OK`:** Array of room objects.

---

### GET `/api/rooms/{id}`
Get a room by ID.

**Response `200 OK`:** Single room object.

---

### PUT `/api/rooms/{id}`
Update a room.

**Request Body:** Same as create.

**Response `200 OK`:** Updated room object.

---

### DELETE `/api/rooms/{id}`
Delete a room.

**Response `204 No Content`**

---

## 8. Absences

**Base path:** `/api/absences`  
**Required roles:** `ROLE_ADMIN`, `ROLE_TEACHER`

---

### POST `/api/absences`
Mark a student as absent.

**Request Body:**
```json
{
  "date": "2026-03-11",
  "status": "UNJUSTIFIED",
  "studentId": 2
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "date": "2026-03-11",
  "status": "UNJUSTIFIED",
  "studentId": 2,
  "studentFullName": "John Doe"
}
```

---

### GET `/api/absences`
Get all absences.

**Response `200 OK`:** Array of absence objects.

---

### GET `/api/absences/{id}`
Get an absence by ID.

**Response `200 OK`:** Single absence object.

---

### GET `/api/absences/student/{studentId}`
Get all absences for a specific student.

**Response `200 OK`:** Array of absence objects.

---

### PATCH `/api/absences/{id}/justify?accepted={true|false}`
Justify or reject an absence justification request.

**Query Param:** `accepted=true` or `accepted=false`

**Response `200 OK`:** Updated absence object with status `JUSTIFIED` or `UNJUSTIFIED`.

---

### DELETE `/api/absences/{id}`
Delete an absence record.

**Response `204 No Content`**

---

## 9. Requests

**Base path:** `/api/requests`  
**Required roles:** All authenticated users

---

### POST `/api/requests`
Submit a new student request (certificate, transcript, etc.).

**Request Body:**
```json
{
  "type": "ENROLLMENT_CERTIFICATE",
  "description": "I need a certificate for visa application",
  "studentId": 2
}
```

**Response `201 Created`:**
```json
{
  "id": 1,
  "type": "ENROLLMENT_CERTIFICATE",
  "description": "I need a certificate for visa application",
  "status": "PENDING",
  "createdAt": "2026-03-11T10:00:00",
  "updatedAt": "2026-03-11T10:00:00",
  "studentId": 2,
  "studentFullName": "John Doe"
}
```

---

### GET `/api/requests`
Get all requests.

**Response `200 OK`:** Array of request objects.

---

### GET `/api/requests/{id}`
Get a request by ID.

**Response `200 OK`:** Single request object.

---

### GET `/api/requests/pending`
Get all pending requests.

**Response `200 OK`:** Array of request objects with `status: "PENDING"`.

---

### GET `/api/requests/student/{studentId}`
Get all requests submitted by a specific student.

**Response `200 OK`:** Array of request objects.

---

### PATCH `/api/requests/{id}/status?status={status}`
Update a request's status (Admin action).

**Query Param:** `status=APPROVED`, `status=REJECTED`, `status=IN_PROGRESS`

**Response `200 OK`:** Updated request object.

---

### DELETE `/api/requests/{id}`
Delete a request.

**Response `204 No Content`**

---

## 10. Enums Reference

### UserRole
| Value | Description |
|-------|-------------|
| `ROLE_ADMIN` | Full access |
| `ROLE_ADMIN_STAFF` | Administrative staff |
| `ROLE_TEACHER` | Teacher |
| `ROLE_STUDENT` | Student |

### RoomType
| Value | Description |
|-------|-------------|
| `LECTURE` | Lecture hall |
| `PRACTICAL` | Lab / practical room |
| `EXAM` | Exam room |

### AbsenceStatus
| Value | Description |
|-------|-------------|
| `UNJUSTIFIED` | Not justified |
| `JUSTIFIED` | Accepted justification |
| `PENDING` | Awaiting review |

### RequestType
| Value | Description |
|-------|-------------|
| `ENROLLMENT_CERTIFICATE` | Certificate of enrollment |
| `TRANSCRIPT` | Academic transcript |
| `COMPLAINT` | Complaint |
| `LEAVE_REQUEST` | Leave of absence |

### RequestStatus
| Value | Description |
|-------|-------------|
| `PENDING` | Awaiting review |
| `IN_PROGRESS` | Being processed |
| `APPROVED` | Approved |
| `REJECTED` | Rejected |

---

## 11. Role-Based Access Control

| Route Pattern | ROLE_ADMIN | ROLE_ADMIN_STAFF | ROLE_TEACHER | ROLE_STUDENT |
|---------------|:---:|:---:|:---:|:---:|
| `POST /api/auth/**` | ✅ | ✅ | ✅ | ✅ |
| `GET/PUT/DELETE /api/users/**` | ✅ | ✅ | ❌ | ❌ |
| `GET/POST/DELETE /api/rooms/**` | ✅ | ✅ | ❌ | ❌ |
| `GET/POST/DELETE /api/teachers/**` | ✅ | ✅ | ❌ | ❌ |
| `GET /api/students/**` | ✅ | ✅ | ✅ | ❌ |
| `GET/POST/DELETE /api/courses/**` | ✅ | ❌ | ✅ | ❌ |
| `GET/POST/DELETE /api/timetables/**` | ✅ | ✅ | ❌ | ❌ |
| `GET/POST/DELETE /api/absences/**` | ✅ | ❌ | ✅ | ❌ |
| `GET/POST/DELETE /api/requests/**` | ✅ | ✅ | ✅ | ✅ |

---

## 12. Angular Service Examples

### Auth Service (`auth.service.ts`)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

const BASE = 'http://localhost:8080/api/auth';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private http: HttpClient) {}

  login(email: string, password: string) {
    return this.http.post<any>(`${BASE}/login`, { email, password }).pipe(
      tap(res => {
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        localStorage.setItem('role', res.role);
      })
    );
  }

  register(payload: any) {
    return this.http.post<any>(`${BASE}/register`, payload);
  }

  refreshToken() {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<any>(`${BASE}/refresh-token`, { refreshToken }).pipe(
      tap(res => localStorage.setItem('accessToken', res.accessToken))
    );
  }

  logout() {
    localStorage.clear();
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }
}
```

### JWT Interceptor (`jwt.interceptor.ts`)
```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { AuthService } from './auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.authService.getToken();
    if (token) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

Register in `app.module.ts`:
```typescript
providers: [
  { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
]
```

### Course Service (`course.service.ts`)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

const BASE = 'http://localhost:8080/api/courses';

@Injectable({ providedIn: 'root' })
export class CourseService {
  constructor(private http: HttpClient) {}

  getAll()                          { return this.http.get<any[]>(BASE); }
  getById(id: number)               { return this.http.get<any>(`${BASE}/${id}`); }
  getByTeacher(teacherId: number)   { return this.http.get<any[]>(`${BASE}/teacher/${teacherId}`); }
  create(dto: any)                  { return this.http.post<any>(BASE, dto); }
  assignTeacher(courseId: number, teacherId: number) {
    return this.http.patch<any>(`${BASE}/${courseId}/assign-teacher/${teacherId}`, {});
  }
  enrollStudent(courseId: number, studentId: number) {
    return this.http.patch<string>(`${BASE}/${courseId}/enroll-student/${studentId}`, {},
      { responseType: 'text' as 'json' });
  }
  delete(id: number)                { return this.http.delete(`${BASE}/${id}`); }
}
```

### Timetable Service (`timetable.service.ts`)
```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

const BASE = 'http://localhost:8080/api/timetables';

@Injectable({ providedIn: 'root' })
export class TimetableService {
  constructor(private http: HttpClient) {}

  create(dto: any)                    { return this.http.post<any>(BASE, dto); }
  getById(id: number)                 { return this.http.get<any>(`${BASE}/${id}`); }
  getStudentSchedule(studentId: number) { return this.http.get<any[]>(`${BASE}/student/${studentId}`); }
  getTeacherSchedule(teacherId: number) { return this.http.get<any[]>(`${BASE}/teacher/${teacherId}`); }
  delete(id: number)                  { return this.http.delete(`${BASE}/${id}`); }

  checkRoomAvailability(roomId: number, date: string, day: string, startTime: string, endTime: string) {
    const params = new HttpParams()
      .set('roomId', roomId)
      .set('date', date)             // format: "2026-05-23"
      .set('day', day)
      .set('startTime', startTime)   // format: "09:00:00"
      .set('endTime', endTime);
    return this.http.get<boolean>(`${BASE}/room-availability`, { params });
  }
}
```

### TypeScript Interfaces (`models.ts`)
```typescript
export type UserRole = 'ROLE_ADMIN' | 'ROLE_ADMIN_STAFF' | 'ROLE_TEACHER' | 'ROLE_STUDENT';
export type AbsenceStatus = 'JUSTIFIED' | 'UNJUSTIFIED' | 'PENDING';
export type RequestStatus = 'PENDING' | 'IN_PROGRESS' | 'APPROVED' | 'REJECTED';
export type RequestType = 'ENROLLMENT_CERTIFICATE' | 'TRANSCRIPT' | 'COMPLAINT' | 'LEAVE_REQUEST';
export type RoomType = 'LECTURE' | 'PRACTICAL' | 'EXAM';

export interface UserResponse {
  idUser: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
}

export interface StudentResponse extends UserResponse {
  studentNumber: string;
  major: string;
}

export interface TeacherResponse extends UserResponse {
  employeeNumber: string;
  speciality: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  email: string;
  role: UserRole;
}

export interface CourseDto {
  id?: number;
  name: string;
  code: string;
  teacherId?: number;
  teacherFullName?: string;
}

export interface TimetableDto {
  id?: number;
  date: string;      // "yyyy-MM-dd"
  day: string;
  startTime: string;   // "HH:mm:ss"
  endTime: string;     // "HH:mm:ss"
  courseId: number;
  courseName?: string;
  roomId: number;
  roomName?: string;
}

export interface RoomDto {
  id?: number;
  name: string;
  capacity: number;
  type: RoomType;
}

export interface AbsenceDto {
  id?: number;
  date: string;         // "YYYY-MM-DD"
  status: AbsenceStatus;
  studentId: number;
  studentFullName?: string;
}

export interface RequestDto {
  id?: number;
  type: RequestType;
  description: string;
  status?: RequestStatus;
  createdAt?: string;
  updatedAt?: string;
  studentId: number;
  studentFullName?: string;
}
```

---

## Common HTTP Error Responses

| Status | Meaning |
|--------|---------|
| `400 Bad Request` | Validation failed (missing/invalid fields) |
| `401 Unauthorized` | No token or expired token |
| `403 Forbidden` | Valid token but insufficient role |
| `404 Not Found` | Resource doesn't exist |
| `409 Conflict` | Duplicate resource (e.g. room already booked) |
| `500 Internal Server Error` | Business logic error (e.g. already enrolled) |

