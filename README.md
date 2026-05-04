# Form Manager (Spring Boot)

A simple form management system for admin to create forms and for employees to submit data.

## Tech

- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Validation, Thymeleaf
- MySQL

## Setup

### Prerequisites

- Java 17+
- Maven
- MySQL 8+

### Database

1. Create database + user:

```sql
source d:/TopCV/create_database.sql
```

2. (Optional) Apply schema manually if you do not want JPA auto-creation:

```sql
source d:/TopCV/db/schema.sql
```

### App config

Update `spring.datasource.*` in `application.yml`.

### Run

```bash
mvn spring-boot:run
```

## Swagger / OpenAPI

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`

## Main Routes

- Admin UI: `/admin/forms`
- Employee UI: `/employee/forms`
- REST API: `/api/forms`, `/api/forms/{id}/submissions`, `/api/forms/{id}/submit`, `/api/submissions`

## Notes

- Validation rules in this demo use sample defaults:
  - `text`: max length 200
  - `number`: 0-100
  - `date`: no past date
  - `color`: hex format `#RRGGBB`
- These are intentionally simple and can be extended per-field.
- Admin UI uses a JSON textarea to manage fields (example included on the edit page).

## Tests

```bash
mvn test
```

## Postman

- Collection: [postman/FormManager.postman_collection.json](postman/FormManager.postman_collection.json)

## Submission checklist

- Source code pushed to GitHub
- README.md with setup/run steps
- Database schema/migration script: [db/schema.sql](db/schema.sql)
- Postman collection: [postman/FormManager.postman_collection.json](postman/FormManager.postman_collection.json)

## API Notes

- `GET /api/forms` supports optional pagination via `page` and `size`.
- `GET /api/forms/active` returns active forms sorted by display order.
- `GET /api/submissions` supports `includeValues=true` to return field values.
- Error responses use a unified JSON format:

```json
{
  "timestamp": "2026-04-30T10:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Submission validation failed",
  "path": "/api/forms/1/submit",
  "details": {
    "fieldErrors": [
      { "fieldId": 10, "label": "Age", "message": "Value must be 0-100" }
    ]
  }
}
```
