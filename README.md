# preschool-api

Spring Boot REST API scaffold based on the project layout used by `authentication-api`.

## Requirements

- Java 17
- Maven 3.9+ (or the included Maven Wrapper)
- MongoDB for application runtime

## Run

Set `MONGODB_URI` to the MongoDB connection URI for the `preschool` database
(including the required credentials and connection options), then start the
application:

```powershell
$env:MONGODB_URI = "<your MongoDB connection URI>"
.\mvnw.cmd spring-boot:run
```

Local development uses HTTP and does not require a keystore. To enable HTTPS in
a deployment, mount a keystore outside the repository and set
`SERVER_SSL_ENABLED=true`, `KEYSTORE_PATH=file:/path/to/keystore.p12`, and
`KEYSTORE_PASSWORD` to the keystore password. Do not copy private keystores
into the source repository.

For local development, the application uses a fixed, public JWT key so it can
start without configuring `JWT_SECRET`. This key is not secure and must not be
used in production; production deployments must override `JWT_SECRET` with
Base64-encoded cryptographically random key material of at least 64 bytes.

The sample endpoint is `GET http://localhost:8080/api/health`.

Authentication is available at `POST /api/auth/login` with a JSON body
containing `username` and `password`. Send the returned token as
`Authorization: Bearer <token>` to call `GET /api/auth/me` or
`POST /api/auth/logout`. Create user records in MongoDB with a BCrypt-hashed
password and a `role` of `CUSTOMER`, `ADMIN`, or `SUPER_ADMIN`. Routes under
`/api/admin/**` require an admin role; other `/api/**` routes require login.

Build and test with `.\mvnw.cmd verify`.

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
