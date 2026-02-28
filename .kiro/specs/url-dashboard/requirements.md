# Requirements Document: URL Dashboard

## Introduction

The URL Dashboard is a simple full-stack integration feature that allows users to save URLs to a PostgreSQL database and display all saved URLs. This feature serves as an integration test to verify communication between the Next.js frontend, Quarkus backend, and PostgreSQL database.

## Glossary

- **URL_Dashboard**: The frontend user interface component that displays saved URLs and provides URL input functionality
- **URL_Service**: The backend REST API service that handles URL persistence and retrieval
- **Saved_URL**: A database entity representing a URL string with metadata (id, url, createdAt)
- **Backend**: The Quarkus-based Kotlin REST API server
- **Frontend**: The Next.js TypeScript application
- **Database**: The PostgreSQL database instance

## Requirements

### Requirement 1: URL Persistence

**User Story:** As a user, I want to save URLs to the database, so that I can store links for later reference.

#### Acceptance Criteria

1. THE URL_Service SHALL accept POST requests at /api/urls with a JSON body containing a url field
2. WHEN a valid URL is submitted, THE URL_Service SHALL store it in the Database with an auto-generated id and timestamp
3. WHEN a URL is successfully saved, THE URL_Service SHALL return HTTP 201 with the created Saved_URL entity
4. IF the url field is empty or missing, THEN THE URL_Service SHALL return HTTP 400 with a descriptive error message
5. THE Database SHALL persist Saved_URL entities in a table named "saved_urls" with columns: id, url, created_at

### Requirement 2: URL Retrieval

**User Story:** As a user, I want to view all saved URLs, so that I can see my collection of stored links.

#### Acceptance Criteria

1. THE URL_Service SHALL accept GET requests at /api/urls
2. WHEN a GET request is received, THE URL_Service SHALL return HTTP 200 with a JSON array of all Saved_URL entities
3. THE URL_Service SHALL order results by created_at in descending order (newest first)
4. WHEN no URLs exist in the Database, THE URL_Service SHALL return HTTP 200 with an empty JSON array

### Requirement 3: Frontend URL Input

**User Story:** As a user, I want to enter URLs through a simple form, so that I can easily save links.

#### Acceptance Criteria

1. THE URL_Dashboard SHALL display an input field for entering URLs
2. THE URL_Dashboard SHALL display a submit button labeled "Save URL"
3. WHEN the submit button is clicked, THE URL_Dashboard SHALL send a POST request to the Backend at /api/urls
4. WHEN the URL is successfully saved, THE URL_Dashboard SHALL clear the input field
5. WHEN the URL is successfully saved, THE URL_Dashboard SHALL refresh the displayed list of URLs
6. IF the Backend returns an error, THEN THE URL_Dashboard SHALL display the error message to the user

### Requirement 4: Frontend URL Display

**User Story:** As a user, I want to see all my saved URLs in a list, so that I can browse my stored links.

#### Acceptance Criteria

1. WHEN the URL_Dashboard loads, THE URL_Dashboard SHALL fetch all Saved_URL entities from the Backend
2. THE URL_Dashboard SHALL display each Saved_URL as a list item showing the url and createdAt timestamp
3. THE URL_Dashboard SHALL format timestamps in a human-readable format
4. WHEN no URLs exist, THE URL_Dashboard SHALL display a message "No URLs saved yet"

### Requirement 5: Backend Testing

**User Story:** As a developer, I want comprehensive backend tests, so that I can verify the API works correctly.

#### Acceptance Criteria

1. THE Backend SHALL include a test class that verifies GET /api/urls returns HTTP 200
2. THE Backend SHALL include a test that verifies POST /api/urls with valid data returns HTTP 201
3. THE Backend SHALL include a test that verifies POST /api/urls with empty url field returns HTTP 400
4. THE Backend SHALL include a test that verifies the round-trip property: a URL posted to the API can be retrieved via GET
5. THE Backend SHALL use H2 in-memory database for test execution

### Requirement 6: Integration Testing

**User Story:** As a developer, I want to verify full-stack integration, so that I can ensure all components work together.

#### Acceptance Criteria

1. THE Frontend SHALL compile without TypeScript errors when running npx tsc --noEmit
2. THE Frontend SHALL build successfully when running npm run build
3. THE Backend SHALL pass all tests when running ./mvnw test
4. WHEN the full stack is running via docker-compose, THE Frontend SHALL successfully communicate with the Backend
5. THE Backend SHALL successfully persist data to the PostgreSQL Database in the docker-compose environment
