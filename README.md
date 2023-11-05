# api-user-management
User Management

# API Endpoints
## User Registration API
This is the documentation for the User Registration API, which allows users to register with their email and other information.
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


## Running the application
```bash
spring-boot:run -Dspring-boot.run.arguments=--DBURI=mongodb://localhost:27017/dev,--SECRET=3A8BFDDEA9AFA32F0E711CCC109D3D4EF50F9D49B134C49F7005E1C4D3D80123141C56,--EXPIRATION=3700000 -f pom.xml
```


---
**README.md created by Felipe Silva de Mello**