# api-user-management

User Management

# API Endpoints

## User Registration API

This is the documentation for the User Registration API, which allows users to register with their email and other
information.

### Register User

Register a user by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users/register`
- **Method**: `POST`
- **Content Type**: `application/json`

#### Request Body

- **Email**: The user's email address.
- **Username**: The user's username.
- **Password**: The user's password.
- #### Example Request Body

```json
{
  "email": "user@example.com",
  "username": "exampleUser",
  "password": "examplePassword"
}
```

#### Example Responses 400, 500, 201

##### HTTP 400

```json
{
  "message": "Email 'user@example.com' already exists."
}
```

##### HTTP 500

```json
{
  "message": "Internal Server Error."
}
```

##### HTTP 201

```json
{
  "message": "User registered successfully."
}
```

---

### Login User

Login a user by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users`
- **Method**: `POST`
- **Content Type**: `application/json`

#### Request Body

- **Email**: The user's email address.
- **Password**: The user's password.
- #### Example Request Body

```json
{
  "email": "user@example.com",
  "password": "examplePassword"
}
```

#### Example Responses 401, 404, 200

##### HTTP 401

```json
{
  "message": "Incorrect Password"
}
```

##### HTTP 404

```json
{
  "message": "Email {email} not found"
}
```

##### HTTP 200

```json
{
  "message": "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2OTkyMDUyMzksInN1YiI6ImthcmVuQGdtYWlsLmNvbSIsImlhdCI6MTY5OTIwMTYzOX0.Xlh1JvFU-8VBVEvw0SWWqaCKjmgyGbVtBykkLUgQT0fx25AeELTl8xn1Kb9M4VdbltNXmPOGGiH5sqs5DQpFzw"
}
```

---

### Update User Password

Update a user password by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `PATCH`
- **Content Type**: `application/json`

#### Request Body

- **Current Password**: The user's current password.
- **New Password**: The user's new password.
- #### Example Request Body

```json
{
  "currentPassword": "examplePassword",
  "newPassword": "newExamplePassword"
}
```

---

### Get user by ID

Get a user by ID.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `GET`
- **Content Type**: `application/json`

---

### Delete user by ID

Delete a user by ID.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `DELETE`
- **Content Type**: `application/json`

## Running the application

```bash
spring-boot:run -Dspring-boot.run.arguments=--DBURI=mongodb+srv://<username>:<password>@camp.s1dnkux.mongodb.net/dev,--SECRET=<SECRETKEY>,--EXPIRATION=<TOKEN_EXPIRY> -f pom.xml
```

---
**README.md created by Felipe Silva de Mello**