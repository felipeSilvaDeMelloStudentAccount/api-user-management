# User Management API

## Endpoints

### User Registration API

This is the documentation for the User Registration API, allowing users to register with their email
and other information.

#### Register User

Register a user by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users/register`
- **Method**: `POST`
- **Content Type**: `application/json`

##### Request Body

- **email**: The user's email address.
- **userName**: The user's username.
- **password**: The user's password.
- **confirmPassword**: The user's password confirmation.

###### Password Policy

- At least 8 chars
- Contains at least one digit
- Contains at least one lower alpha char and one upper alpha char
- Contains at least one char within a set of special chars (@#%$^ etc.)

##### Example Request Body

```json
{
  "email": "user@example.com",
  "userName": "exampleUser",
  "password": "examplePassword",
  "confirmPassword": "exampleconfirmPassword@123"
}
```

##### Example Responses 400, 500, 201

###### HTTP 400

```json
{
  "message": "Email 'user@example.com' already exists."
}
```

###### HTTP 500

```json
{
  "message": "Internal Server Error."
}
```

###### HTTP 201

```json
{
  "message": "User registered successfully."
}
```

---

#### Login User

Login a user by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users`
- **Method**: `POST`
- **Content Type**: `application/json`

##### Request Body

- **email**: The user's email address.
- **password**: The user's password.

##### Example Request Body

```json
{
  "email": "user@example.com",
  "password": "examplePassword"
}
```

##### Example Responses 401, 404, 200

###### HTTP 401

```json
{
  "message": "Incorrect Password"
}
```

###### HTTP 404

```json
{
  "message": "Email {email} not found"
}
```

###### HTTP 200

```json
{
  "message": "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2OTkyMDUyMzksInN1YiI6ImthcmVuQGdtYWlsLmNvbSIsImlhdCI6MTY5OTIwMTYzOX0.Xlh1JvFU-8VBVEvw0SWWqaCKjmgyGbVtBykkLUgQT0fx25AeELTl8xn1Kb9M4VdbltNXmPOGGiH5sqs5DQpFzw"
}
```

---

#### Update User Password

Update a user password by providing the necessary details.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `PATCH`
- **Content Type**: `application/json`

##### Request Header

- **Authorization**: Bearer Token from login

##### Request Body

- **currentPassword**: The user's current password.
- **newPassword**: The user's new password.

##### Example Request Body

```json
{
  "currentPassword": "examplePassword",
  "newPassword": "newExamplePassword"
}
```

---

#### Get User by ID

Get a user by ID.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `GET`
- **Content Type**: `application/json`

##### Request Header

- **Authorization**: Bearer Token from login

---

#### Delete User by ID

Delete a user by ID.

- **URL**: `http://localhost:9001/api/v1/users/{userid}`
- **Method**: `DELETE`
- **Content Type**: `application/json`

##### Request Header

- **Authorization**: Bearer JWT...

## Software Requirements to Run the Application

### Java Development Kit (JDK) 21 or Greater

- Visit the Oracle JDK or OpenJDK website to download and install the JDK.
  https://www.oracle.com/java/technologies/downloads/
- Set the JAVA_HOME environment variable to the JDK installation directory.
- Add the JDK bin directory to the PATH environment variable.
- Verify the JDK installation by running the following command in a terminal window:

  ```bash
  java -version
  ```

### Maven 3.6.3 or Greater

- Visit the Maven website to download and install Maven: https://maven.apache.org/
- Set the MAVEN_HOME environment variable to the Maven installation directory.
- Add the Maven bin directory to the PATH environment variable.
- Verify the Maven installation by running the following command in a terminal window:

  ```bash
  mvn -version
  ```

## Running the Application

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--DBURI=mongodb+srv://<username>:<password>@camp.s1dnkux.mongodb.net/dev,--SECRET=<SECRETKEY>,--EXPIRATION=<TOKEN_EXPIRY> -f pom.xml
```

## Disclaimer

This software is intended for use solely within the scope of my college project.
It is not to be sold, distributed, or utilized for any commercial purposes.
All rights to this software are reserved, and any unauthorized use or reproduction is strictly
prohibited.
By using this software, you acknowledge and agree to adhere to these terms.
**Note:** Please consult with legal professionals to ensure that the disclaimer aligns with the
legal requirements in your jurisdiction.

**README.md created by Felipe Silva de Mello**