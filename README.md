# Assignment for GT
A RESTful API developed using Spring Boot to manage user data, including a file upload feature to import user information from CSV files.

## Technologies

- Spring Boot
- Spring Data JPA
- H2 Database (file-based storage)

## Testing

- JMeter was used to test the asynchronous functionality of the file upload feature with the following setup:
 -  5 concurrent requests
 -  CSV file with 1000 records

[![J Meter ASync Test Results](https://raw.githubusercontent.com/kuroneko420/assignmentforgt/main/jmetertestresult/test1.PNG "J Meter ASync Test Results")](https://raw.githubusercontent.com/kuroneko420/assignmentforgt/main/jmetertestresult/test1.PNG "J Meter ASync Test Results")

## Setup

1. Clone the repository to your local machine.
2. Import the project into your favorite IDE.
3. Ensure that the application.properties file is set up correctly for a file-based H2 database.
4. If you encounter issues with the database initialization, you may need to install the H2 console separately and configure it to create the database schema.
5. Run the project using the IDE or the command line (./mvnw spring-boot:run or mvn spring-boot:run).
6. The API will be available at http://localhost:8080/api.
7. The H2 Database console will be available at http://localhost:8080/h2-console/, the username is sa, no password.

## Usage
### Retrieve Users

GET /api/users

Query Parameters:

    min (default: 0.0): Minimum salary value
    max (default: 4000.0): Maximum salary value
    offset (default: 0): Offset for pagination
    limit (default: 2147483647): Limit for pagination
    sort (default: ""): Sort by NAME or SALARY

### Upload CSV File

POST /api/upload

Content-Type: multipart/form-data

Form field name: file

The CSV file should have the following format:

    First row as header with columns NAME and SALARY
    Name is a text column
    Salary is a floating-point number
    Salary must be >= 0.0; rows with salary < 0.0 are ignored
    Rows with duplicate names will update the existing user's salary
    Improperly structured CSV files or files with bad formatting will be rejected
