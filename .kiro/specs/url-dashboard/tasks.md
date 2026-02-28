# Implementation Plan: URL Dashboard

## Overview

Simple full-stack integration test: save URLs via frontend, store in PostgreSQL via backend, display the list. Minimal implementation focused on verifying the complete stack works together.

## Tasks

- [x] 1. Create SavedUrl entity
  - Create `backend/src/main/kotlin/org/acme/SavedUrl.kt`
  - Add entity with id, url, createdAt fields
  - Use Panache pattern with table name "saved_urls"
  - _Requirements: 1.2, 1.5_

- [x] 2. Create UrlResource REST API
  - Create `backend/src/main/kotlin/org/acme/UrlResource.kt`
  - Add GET /api/urls endpoint that returns all URLs
  - Add POST /api/urls endpoint that saves URLs
  - Return 400 if url field is empty or missing
  - _Requirements: 1.1, 1.3, 1.4, 2.1, 2.2, 2.4_

- [x] 3. Write backend tests
  - Create `backend/src/test/kotlin/org/acme/UrlResourceTest.kt`
  - Test GET /api/urls returns 200
  - Test POST with valid URL returns 201
  - Test POST with empty URL returns 400
  - Test round-trip: POST then GET retrieves the URL
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [x] 4. Checkpoint - Backend tests pass
  - Run `cd backend && ./mvnw test`
  - Ensure all tests pass, ask the user if questions arise

- [x] 5. Create frontend URL dashboard
  - Modify `frontend/src/app/page.tsx`
  - Add input field and "Save URL" button
  - Add list display for saved URLs
  - Fetch URLs on page load
  - POST new URLs to backend
  - Clear input and reload list after save
  - Show "No URLs saved yet" when list is empty
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.4_

- [x] 6. Checkpoint - Frontend builds successfully
  - Run `cd frontend && npx tsc --noEmit`
  - Run `cd frontend && npm run build`
  - Ensure no errors, ask the user if questions arise

- [x] 7. Final integration verification
  - Verify docker-compose.yml includes all services
  - Confirm CORS is configured in backend
  - Document manual testing steps for user
  - _Requirements: 6.4, 6.5_

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Backend uses H2 for tests, PostgreSQL for docker-compose
- Frontend uses hardcoded API URL: http://localhost:8080
- No error handling UI needed for MVP - just basic functionality
- Focus is on integration verification, not production-ready code
