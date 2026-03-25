# 🧪 SmartCompus – Postman Testing Guide

> **Base URL:** `http://localhost:8080`  
> **Content-Type:** `application/json` for all requests with a body

---

## ⚠️ IMPORTANT: How Authentication Works

This API uses **JWT (Bearer Token)**. You must:

1. **Register** a user (no token needed)
2. **Login** to get an `accessToken` (no token needed)
3. **Add the token** to ALL other requests

### How to add the token in Postman:
1. Go to the **Authorization** tab of your request
2. Select Type → **Bearer Token**
3. Paste the `accessToken` value

Or manually add a header:
```
Key:   Authorization
Value: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 💡 Postman Tip – Auto-save token:
In the **Login** request → go to **Scripts → Post-response** tab → add:
```javascript
var jsonData = pm.response.json();
pm.environment.set("accessToken", jsonData.accessToken);
pm.environment.set("refreshToken", jsonData.refreshToken);
```
Then in all other requests, set Bearer Token to `{{accessToken}}`. The token will auto-update after every login.

---

## 🔑 Role Permissions

| Endpoint | Allowed Roles |
|----------|--------------|
| `/api/auth/**` | ✅ Everyone (no token) |
| `/api/users/**` | ADMIN, ADMIN_STAFF |
| `/api/rooms/**` | ADMIN, ADMIN_STAFF |
| `/api/courses/**` | ADMIN, TEACHER |
| `/api/timetables/**` | ADMIN, ADMIN_STAFF |
| `/api/absences/**` | ADMIN, TEACHER |
| `/api/students/**` | ADMIN, ADMIN_STAFF, TEACHER |
| `/api/teachers/**` | ADMIN, ADMIN_STAFF |
| `/api/requests/**` | Any logged-in user |

> If you get **403 Forbidden** → you're using a token with the wrong role for that endpoint.  
> If you get **401 Unauthorized** → your token is missing, expired, or invalid.

---

## 📋 Test Order (follow this order)

1. Register Admin
2. Login as Admin → copy `accessToken`
3. Register Student
4. Register Teacher
5. Create Room (admin token)
6. Create Course (admin token)
7. Assign Teacher to Course (admin token)
8. Enroll Student in Course (admin token)
9. Create Timetable (admin token)
10. Create Absence (admin token)
11. Create Request (any token)

---

## 🔓 1. Auth Endpoints (NO token needed)

### Register Admin
```
POST http://localhost:8080/api/auth/register
```
```json
{
  "firstName": "Karim",
  "lastName": "Admin",
  "email": "karim.admin@school.ma",
  "password": "admin789",
  "role": "ROLE_ADMIN"
}
```

### Register Student
```
POST http://localhost:8080/api/auth/register
```
```json
{
  "firstName": "Ali",
  "lastName": "Hassan",
  "email": "ali.hassan@school.ma",
  "password": "secret123",
  "role": "ROLE_STUDENT",
  "studentNumber": "STU-2024-001",
  "major": "Computer Science"
}
```

### Register Teacher
```
POST http://localhost:8080/api/auth/register
```
```json
{
  "firstName": "Sara",
  "lastName": "Benali",
  "email": "sara.benali@school.ma",
  "password": "teach456",
  "role": "ROLE_TEACHER",
  "employeeNumber": "EMP-2024-001",
  "speciality": "Mathematics"
}
```

### Register Admin Staff
```
POST http://localhost:8080/api/auth/register
```
```json
{
  "firstName": "Omar",
  "lastName": "Staff",
  "email": "omar.staff@school.ma",
  "password": "staff123",
  "role": "ROLE_ADMIN_STAFF"
}
```

### Login
```
POST http://localhost:8080/api/auth/login
```
```json
{
  "email": "karim.admin@school.ma",
  "password": "admin789"
}
```
✅ **Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "karim.admin@school.ma",
  "role": "ROLE_ADMIN"
}
```
📌 **Copy the `accessToken` and use it in all requests below.**

### Refresh Token (when accessToken expires)
```
POST http://localhost:8080/api/auth/refresh-token
```
```json
{
  "refreshToken": "paste_your_refreshToken_here"
}
```
✅ Returns a new `accessToken` + `refreshToken`.

---

## 👤 2. Users — `/api/users` (🔒 ADMIN, ADMIN_STAFF)

> Use **admin** or **admin_staff** token for all these.

### Get User by ID
```
GET http://localhost:8080/api/users/1
```

### Get User by Email
```
GET http://localhost:8080/api/users/email?email=ali.hassan@school.ma
```

### Get Users by Role
```
GET http://localhost:8080/api/users/role/ROLE_STUDENT
GET http://localhost:8080/api/users/role/ROLE_TEACHER
GET http://localhost:8080/api/users/role/ROLE_ADMIN
GET http://localhost:8080/api/users/role/ROLE_ADMIN_STAFF
```

### Update User
```
PUT http://localhost:8080/api/users/1
```
```json
{
  "firstName": "Karim Updated",
  "lastName": "Admin",
  "email": "karim.admin@school.ma",
  "role": "ROLE_ADMIN"
}
```

### Change Password
```
PATCH http://localhost:8080/api/users/1/password
```
Body (raw text):
```
newPassword123
```

### Delete User
```
DELETE http://localhost:8080/api/users/3
```

---

## 🏫 3. Rooms — `/api/rooms` (🔒 ADMIN, ADMIN_STAFF)

### Create Room
```
POST http://localhost:8080/api/rooms
```
```json
{
  "name": "Room A1",
  "capacity": 30,
  "type": "LECTURE"
}
```
> Type values: `LECTURE`, `PRACTICAL`, `EXAM`

### Create Another Room
```
POST http://localhost:8080/api/rooms
```
```json
{
  "name": "Lab B2",
  "capacity": 20,
  "type": "PRACTICAL"
}
```

### Get All Rooms
```
GET http://localhost:8080/api/rooms
```

### Get Room by ID
```
GET http://localhost:8080/api/rooms/1
```

### Update Room
```
PUT http://localhost:8080/api/rooms/1
```
```json
{
  "name": "Room A1 Updated",
  "capacity": 35,
  "type": "LECTURE"
}
```

### Delete Room
```
DELETE http://localhost:8080/api/rooms/2
```

---

## 📚 4. Courses — `/api/courses` (🔒 ADMIN, TEACHER)

### Create Course
```
POST http://localhost:8080/api/courses
```
```json
{
  "name": "Mathematics 101",
  "code": "MATH101",
  "teacherId": 3
}
```
> `teacherId` = the ID of the teacher you registered (check with GET /api/users/role/ROLE_TEACHER)

### Create Another Course
```
POST http://localhost:8080/api/courses
```
```json
{
  "name": "Physics 201",
  "code": "PHY201"
}
```

### Get All Courses
```
GET http://localhost:8080/api/courses
```

### Get Course by ID
```
GET http://localhost:8080/api/courses/1
```

### Get Courses by Teacher
```
GET http://localhost:8080/api/courses/teacher/3
```

### Assign Teacher to Course
```
PATCH http://localhost:8080/api/courses/2/assign-teacher/3
```

### Enroll Student in Course
```
PATCH http://localhost:8080/api/courses/1/enroll-student/2
```
> `2` = student ID, `1` = course ID

### Delete Course
```
DELETE http://localhost:8080/api/courses/1
```

---

## 📅 5. Timetables — `/api/timetables` (🔒 ADMIN, ADMIN_STAFF)

### Create Timetable Entry
```
POST http://localhost:8080/api/timetables
```
```json
{
  "date": "2026-05-23",
  "day": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "11:00:00",
  "courseId": 1,
  "roomId": 1
}
```
> Day values: `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY`
> Date format: `yyyy-MM-dd`

### Create Another Timetable Entry
```
POST http://localhost:8080/api/timetables
```
```json
{
  "date": "2026-05-28",
  "day": "WEDNESDAY",
  "startTime": "14:00:00",
  "endTime": "16:00:00",
  "courseId": 1,
  "roomId": 1
}
```

### Get Timetable by ID
```
GET http://localhost:8080/api/timetables/1
```

### Get Student Schedule
```
GET http://localhost:8080/api/timetables/student/2
```

### Get Teacher Schedule
```
GET http://localhost:8080/api/timetables/teacher/3
```

### Check Room Availability
```
GET http://localhost:8080/api/timetables/room-availability?roomId=1&date=2026-05-23&day=MONDAY&startTime=09:00:00&endTime=11:00:00
```

### Delete Timetable
```
DELETE http://localhost:8080/api/timetables/1
```

---

## ❌ 6. Absences — `/api/absences` (🔒 ADMIN, TEACHER)

### Mark Student Absent
```
POST http://localhost:8080/api/absences
```
```json
{
  "date": "2026-03-09",
  "status": "PENDING",
  "studentId": 2
}
```
> Status values: `JUSTIFIED`, `UNJUSTIFIED`, `PENDING`

### Get All Absences
```
GET http://localhost:8080/api/absences
```

### Get Absence by ID
```
GET http://localhost:8080/api/absences/1
```

### Get Absences by Student
```
GET http://localhost:8080/api/absences/student/2
```

### Justify Absence
```
PATCH http://localhost:8080/api/absences/1/justify?accepted=true
```

### Delete Absence
```
DELETE http://localhost:8080/api/absences/1
```

---

## 📝 7. Requests — `/api/requests` (🔒 Any logged-in user)

### Submit Request
```
POST http://localhost:8080/api/requests
```
```json
{
  "type": "ENROLLMENT_CERTIFICATE",
  "description": "I need an enrollment certificate for visa application",
  "status": "PENDING",
  "studentId": 2
}
```
> Type values: `ENROLLMENT_CERTIFICATE`, `TRANSCRIPT`, `COMPLAINT`, `LEAVE_REQUEST`  
> Status values: `PENDING`, `IN_PROGRESS`, `APPROVED`, `REJECTED`

### Get All Requests
```
GET http://localhost:8080/api/requests
```

### Get Request by ID
```
GET http://localhost:8080/api/requests/1
```

### Get Pending Requests
```
GET http://localhost:8080/api/requests/pending
```

### Get Requests by Student
```
GET http://localhost:8080/api/requests/student/2
```

### Update Request Status
```
PATCH http://localhost:8080/api/requests/1/status?status=APPROVED
```

### Delete Request
```
DELETE http://localhost:8080/api/requests/1
```

---

## 🎓 8. Students — `/api/students` (🔒 ADMIN, ADMIN_STAFF, TEACHER)

### Get Student by Number
```
GET http://localhost:8080/api/students/number/STU-2024-001
```

### Get Students by Major
```
GET http://localhost:8080/api/students/major/Computer Science
```

---

## 👨‍🏫 9. Teachers — `/api/teachers` (🔒 ADMIN, ADMIN_STAFF)

### Get Teacher by Employee Number
```
GET http://localhost:8080/api/teachers/employee/EMP-2024-001
```

### Get Teachers by Speciality
```
GET http://localhost:8080/api/teachers/speciality/Mathematics
```

---

## 🔥 Quick Full Test (copy-paste in order)

| # | Method | URL | Token | Body |
|---|--------|-----|-------|------|
| 1 | POST | `/api/auth/register` | ❌ | `{"firstName":"Karim","lastName":"Admin","email":"karim.admin@school.ma","password":"admin789","role":"ROLE_ADMIN"}` |
| 2 | POST | `/api/auth/login` | ❌ | `{"email":"karim.admin@school.ma","password":"admin789"}` |
| 3 | POST | `/api/auth/register` | ❌ | `{"firstName":"Ali","lastName":"Hassan","email":"ali.hassan@school.ma","password":"secret123","role":"ROLE_STUDENT","studentNumber":"STU-2024-001","major":"Computer Science"}` |
| 4 | POST | `/api/auth/register` | ❌ | `{"firstName":"Sara","lastName":"Benali","email":"sara.benali@school.ma","password":"teach456","role":"ROLE_TEACHER","employeeNumber":"EMP-2024-001","speciality":"Mathematics"}` |
| 5 | POST | `/api/rooms` | ✅ Admin | `{"name":"Room A1","capacity":30,"type":"LECTURE"}` |
| 6 | POST | `/api/courses` | ✅ Admin | `{"name":"Mathematics 101","code":"MATH101","teacherId":3}` |
| 7 | PATCH | `/api/courses/1/enroll-student/2` | ✅ Admin | — |
| 8 | POST | `/api/timetables` | ✅ Admin | `{"day":"MONDAY","startTime":"09:00:00","endTime":"11:00:00","courseId":1,"roomId":1}` |
| 9 | POST | `/api/absences` | ✅ Admin | `{"date":"2026-03-09","status":"PENDING","studentId":2}` |
| 10 | POST | `/api/requests` | ✅ Admin | `{"type":"ENROLLMENT_CERTIFICATE","description":"Need certificate","status":"PENDING","studentId":2}` |

> **Note:** The `teacherId` and `studentId` depend on the order of registration. Check the IDs returned in the register responses. Typically: Admin=1, Student=2, Teacher=3.

---

## ❓ Troubleshooting

| Error | Cause | Fix |
|-------|-------|-----|
| **401 Unauthorized** | No token or expired token | Login again, copy new `accessToken` |
| **403 Forbidden** | Wrong role for this endpoint | Use a token with the correct role (see permissions table above) |
| **400 Bad Request** | Invalid JSON body | Check the JSON format and required fields |
| **500 Internal Server Error** | Server-side error | Check Docker logs: `docker logs smartcampus` |

