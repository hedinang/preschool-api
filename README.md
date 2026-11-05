# preschool-api

Spring Boot REST API for the An Nhiên preschool portal. It follows a
controller/service/repository/model structure and provides data APIs for the
screens in `preschool-web`.

## Requirements

- Java 17
- Java 17 (set `JAVA_HOME` to a JDK 17 installation)
- MongoDB

## Run

Set `MONGODB_URI` to the MongoDB connection URI for the `preschool` database,
then start the application:

```powershell
$env:MONGODB_URI = "<your MongoDB connection URI>"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\mvnw.cmd spring-boot:run
```

Local development uses HTTP on port `7000`; an additional HTTP connector
listens on port `8400`. To enable HTTPS in a deployment, mount a keystore
outside the repository and set `SERVER_SSL_ENABLED=true`,
`KEYSTORE_PATH=file:/path/to/keystore.p12`, and `KEYSTORE_PASSWORD`. Do not
copy private keystores into the source repository.

For local development, the application uses a fixed, public JWT key so it can
start without configuring `JWT_SECRET`. This key is not secure and must not be
used in production; production deployments must override `JWT_SECRET` with
Base64-encoded cryptographically random key material of at least 64 bytes.

The health endpoint is `GET http://localhost:7000/api/health`.

Authentication is available at `POST /api/auth/login` with a JSON body
containing `username` (or `email`) and `password`. Send the returned token as
`Authorization: Bearer <token>` to call `GET /api/auth/me` or
`POST /api/auth/logout`. Create user records in MongoDB with a BCrypt-hashed
password and a `role` of `ADMIN`, `TEACHER`, `PARENT`, `STUDENT`, or
`SUPER_ADMIN`. `CUSTOMER` remains accepted for existing accounts.

On startup, the application seeds the An Nhiên demo students, teachers,
classrooms, attendance, weekly schedules, tuition invoices, notices, and demo
accounts when matching records do not already exist. The demo accounts use
`123456` as their password:

| Role | Username |
|---|---|
| Admin | `admin@annhien.vn` |
| Teacher | `maiphuong@annhien.vn` |
| Parent | `ngocanh@annhien.vn` |
| Student | `minhkhoi@annhien.vn` |

Set `PRESCHOOL_SEED_DEMO_DATA=false` to disable demo seeding, especially when
using a production database. Existing records are not overwritten.

## Preschool portal API

All responses use the existing `{ "status", "message", "data" }` envelope.
Requests other than health and login require a bearer token. Administrators
manage school records, teachers are scoped to their assigned classroom, and
parents can access children linked to their accounts.

Administrators can list login accounts with `POST /api/user/list`; its body
accepts `sequence`, `limit`, `direction`, optional `role` and `search` filters.
Accounts are fetched when the management page opens and fetched again when the
role filter changes. Use `POST /api/user`, `PUT /api/user/{id}`, and
`DELETE /api/user/{id}` to create, update, and disable accounts. Supported
managed roles are `ADMIN`, `TEACHER`, and `PARENT`; deletion marks accounts
`DELETED`. User profiles include two optional phone numbers, permanent address,
and gender. A user's two phone numbers must differ, and phone number 1 must be
unique among active accounts of the same role; it may be shared across roles.
Student accounts cannot log in.

Parents can declare a child with `POST /api/student`; the API links the child
to the authenticated parent and leaves classroom assignment to the school.
Administrators can search parent accounts through the `role` and `search`
filters on `POST /api/user/list`, then link a student by its `parentUserId`.
The API resolves the selected active parent account and fills the linked parent
name and email. Administrators and teachers can list students, and teachers can
update records within their assigned classroom. Only administrators can delete
student records; deletion marks the record `DELETED`.

| Screen/data | Endpoint | Main operations |
|---|---|---|
| Students | `/api/students` | `GET`, `POST`; item `GET`, `PUT`, `DELETE` |
| Parents | `/api/parents` | `GET` family summaries grouped from student contacts |
| Teachers | `/api/teachers` | `GET`, `POST`; item `PUT`, `DELETE` |
| Classes | `/api/classes` | `GET`, `POST`; item `PUT`, `DELETE` |
| Weekly schedule | `/api/schedule?classroom=Mầm%201` | `GET`, `POST`; item `PUT`, `DELETE` |
| Attendance | `/api/attendance?date=2026-11-04` | `GET`; `PUT` a batch of daily records |
| Tuition | `/api/tuition?month=2026-11` | `GET`, `POST`; item `PUT`; `POST /remind` for outstanding invoices |
| Notices | `/api/messages` | `GET`, `POST` |
| Dashboard | `/api/dashboard/summary?date=2026-11-04&month=2026-11` | `GET` |

Student fields correspond to the web portal (`name`, `gender`, `age`,
`classroom`, `parent`, `phone`, `status`, `avatar`, `color`). Link parent
accounts with `parentUserId` or `parentEmail`, and teacher accounts with their
`classroom`.

## Source layout

```text
org.example.preschool
├── configuration
├── controller
├── mapper
├── model
│   ├── dto
│   ├── entity
│   └── request
├── repository
├── service
│   └── impl
└── util
    ├── exception
    └── response
```

MongoDB collections are created when documents are first written.

Build and test with `.\mvnw.cmd verify`.
