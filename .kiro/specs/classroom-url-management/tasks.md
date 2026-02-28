# Implementation Plan: Classroom URL Management

## Overview

This implementation plan breaks down the Classroom URL Management feature into discrete, sequential coding tasks. The feature enables teachers to create classrooms and manage URL lists for future DNS filtering. Implementation follows the add-only approach with Kotlin + Quarkus backend and TypeScript + Next.js frontend.

## Tasks

- [x] 1. Set up backend database entities and schema
  - [x] 1.1 Create Classroom entity with JPA annotations
    - Define Classroom entity with id, name, description, createdAt fields
    - Add OneToMany relationship to ClassroomUrl
    - Use snake_case for database columns, camelCase for Kotlin properties
    - _Requirements: 1.2, 1.3, 1.5, 8.1_
  
  - [x] 1.2 Create ClassroomUrl entity with JPA annotations
    - Define ClassroomUrl entity with id, classroom, url, urlType, createdAt fields
    - Add ManyToOne relationship to Classroom with LAZY fetch
    - Add unique constraint on (classroom_id, url) to prevent duplicates
    - _Requirements: 4.3, 4.5, 4.8, 7.1, 8.1_
  
  - [x] 1.3 Verify database schema generation
    - Start Quarkus in dev mode to trigger schema generation
    - Verify tables created with correct columns and constraints
    - Check foreign key cascade behavior
    - _Requirements: 8.1, 8.3_

- [x] 2. Implement URL validation and normalization utility
  - [x] 2.1 Create UrlValidator object with validation and normalization functions
    - Implement isValid() to check domain format using regex
    - Implement normalize() to add protocol and lowercase URL
    - Implement extractDomain() to extract domain from full URL
    - _Requirements: 4.2, 6.1, 6.2, 6.4, 7.1_
  
  - [x] 2.2 Write unit tests for UrlValidator
    - Test valid formats: "example.com", "https://example.com", "https://example.com/path"
    - Test normalization: "EXAMPLE.COM" → "https://example.com"
    - Test invalid formats: empty string, invalid domain, special characters
    - Test edge cases: maximum length URLs, URLs with ports
    - _Requirements: 6.1, 6.2, 6.4, 6.5_

- [x] 3. Implement backend DTOs and request/response models
  - [x] 3.1 Create request and response data classes
    - Define CreateClassroomRequest with name and description
    - Define AddUrlRequest with url field
    - Define ClassroomListResponse with id, name, description, urlCount, createdAt
    - Define ClassroomDetailResponse with id, name, description, urls list, createdAt
    - Define UrlResponse with id, url, urlType, createdAt
    - _Requirements: 1.2, 2.2, 3.2, 4.3_

- [x] 4. Implement ClassroomResource REST API endpoints
  - [x] 4.1 Create ClassroomResource class with EntityManager injection
    - Set up @Path("/api/classrooms") with JSON produces/consumes
    - Inject EntityManager for database operations
    - Add logger for error tracking
    - _Requirements: 1.2, 2.2, 3.2, 4.3_
  
  - [x] 4.2 Implement POST /api/classrooms endpoint
    - Validate classroom name (not empty, max 100 chars)
    - Create Classroom entity and persist with @Transactional
    - Return 201 with ClassroomDetailResponse on success
    - Return 400 with error message on validation failure
    - _Requirements: 1.2, 1.3, 1.4, 1.5_
  
  - [x] 4.3 Implement GET /api/classrooms endpoint
    - Query all classrooms ordered by createdAt DESC
    - Calculate URL count for each classroom using subquery or join
    - Return 200 with List<ClassroomListResponse>
    - _Requirements: 2.2, 2.3_
  
  - [x] 4.4 Implement GET /api/classrooms/{id} endpoint
    - Query classroom by id with eager fetch of URLs
    - Return 404 if classroom not found
    - Return 200 with ClassroomDetailResponse including all URLs
    - _Requirements: 3.2, 3.3_
  
  - [x] 4.5 Implement POST /api/classrooms/{id}/urls endpoint
    - Validate URL using UrlValidator.isValid()
    - Normalize URL using UrlValidator.normalize()
    - Check if classroom exists, return 404 if not
    - Check for duplicate URL in classroom, return 400 if duplicate
    - Create ClassroomUrl entity and persist with @Transactional
    - Return 201 with UrlResponse on success
    - Return 400 with error message on validation or duplicate
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6, 6.1, 6.2_

- [x] 5. Implement backend unit tests
  - [x] 5.1 Write unit tests for POST /api/classrooms
    - Test successful creation with valid name and description
    - Test creation with name at exactly 100 characters
    - Test 400 error when name is empty
    - Test 400 error when name exceeds 100 characters
    - Test creation with null description
    - _Requirements: 1.2, 1.3, 1.4_
  
  - [x] 5.2 Write unit tests for GET /api/classrooms
    - Test empty list when no classrooms exist
    - Test single classroom in list
    - Test multiple classrooms ordered by creation date (newest first)
    - Test URL count accuracy
    - _Requirements: 2.2, 2.3_
  
  - [x] 5.3 Write unit tests for GET /api/classrooms/{id}
    - Test successful retrieval with no URLs
    - Test successful retrieval with multiple URLs
    - Test 404 error for non-existent classroom
    - _Requirements: 3.2, 3.3_
  
  - [x] 5.4 Write unit tests for POST /api/classrooms/{id}/urls
    - Test successful URL addition with various formats
    - Test 400 error when URL is empty
    - Test 400 error when URL format is invalid
    - Test 400 error when URL is duplicate
    - Test 404 error when classroom doesn't exist
    - Test URL at maximum length (2048 chars)
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6_

- [ ] 6. Implement backend property-based tests
  - [ ] 6.1 Write property test for Property 1: Classroom Creation Persistence
    - **Property 1: Classroom Creation Persistence**
    - **Validates: Requirements 1.2, 8.1**
    - Generate random valid classroom names (1-100 chars)
    - Create classroom, retrieve it, verify name and description match
  
  - [ ] 6.2 Write property test for Property 2: Classroom Name Validation
    - **Property 2: Classroom Name Validation**
    - **Validates: Requirements 1.3, 1.4**
    - Generate invalid names (empty or >100 chars)
    - Verify all return 400 error
  
  - [ ]* 6.3 Write property test for Property 3: Classroom Metadata Assignment
    - **Property 3: Classroom Metadata Assignment**
    - **Validates: Requirements 1.5, 7.3**
    - Create classrooms with random data
    - Verify all have non-null id and createdAt
  
  - [ ]* 6.4 Write property test for Property 4: Classroom List Ordering
    - **Property 4: Classroom List Ordering**
    - **Validates: Requirements 2.2**
    - Create multiple classrooms with delays
    - Verify list returns newest first
  
  - [ ]* 6.5 Write property test for Property 5: Classroom List Completeness
    - **Property 5: Classroom List Completeness**
    - **Validates: Requirements 2.3**
    - Create classrooms with random data
    - Verify list response includes all required fields
  
  - [ ]* 6.6 Write property test for Property 6: Classroom Detail Retrieval
    - **Property 6: Classroom Detail Retrieval**
    - **Validates: Requirements 3.2**
    - Create classroom with random URLs
    - Verify detail response includes all URLs
  
  - [ ] 6.7 Write property test for Property 7: URL Addition Persistence
    - **Property 7: URL Addition Persistence**
    - **Validates: Requirements 4.3, 8.1**
    - Generate random valid URLs
    - Add to classroom, retrieve classroom, verify URL present
  
  - [ ] 6.8 Write property test for Property 8: URL Validation
    - **Property 8: URL Validation**
    - **Validates: Requirements 4.2, 4.4, 6.4, 6.5**
    - Generate invalid URLs (empty, no domain, special chars)
    - Verify all return 400 error
  
  - [ ]* 6.9 Write property test for Property 9: Duplicate URL Prevention
    - **Property 9: Duplicate URL Prevention**
    - **Validates: Requirements 4.5, 4.6**
    - Add URL to classroom, attempt to add same URL again
    - Verify second attempt returns 400 error
  
  - [ ]* 6.10 Write property test for Property 10: URL Metadata Assignment
    - **Property 10: URL Metadata Assignment**
    - **Validates: Requirements 4.8, 7.3**
    - Add URLs with random data
    - Verify all have non-null id and createdAt
  
  - [ ]* 6.11 Write property test for Property 11: URL Format Acceptance
    - **Property 11: URL Format Acceptance**
    - **Validates: Requirements 6.1**
    - Generate URLs in various formats (with/without protocol, with path)
    - Verify all valid formats are accepted
  
  - [ ]* 6.12 Write property test for Property 12: URL Normalization Consistency
    - **Property 12: URL Normalization Consistency**
    - **Validates: Requirements 6.2, 7.1**
    - Generate equivalent URLs (different cases, protocols)
    - Verify normalized to same format and treated as duplicates

- [x] 7. Checkpoint - Ensure backend tests pass
  - Backend tests are green

- [x] 8. Set up frontend TypeScript interfaces and API client
  - [x] 8.1 Create TypeScript interfaces for data models
    - Define Classroom, ClassroomDetail, ClassroomUrl interfaces
    - Define CreateClassroomRequest, AddUrlRequest interfaces
    - Place in shared types file or component files
    - _Requirements: 1.2, 2.3, 3.2, 4.3_
  
  - [x] 8.2 Create API client functions for classroom operations
    - Implement fetchClassrooms() to GET /api/classrooms
    - Implement fetchClassroomDetail(id) to GET /api/classrooms/{id}
    - Implement createClassroom(data) to POST /api/classrooms
    - Implement addUrlToClassroom(classroomId, url) to POST /api/classrooms/{id}/urls
    - Use environment variable for API_URL with localhost fallback
    - Add proper error handling and type safety
    - _Requirements: 1.2, 2.2, 3.2, 4.3_

- [x] 9. Implement frontend classroom list page
  - [x] 9.1 Create /app/classrooms/page.tsx for classroom list
    - Fetch classrooms on component mount
    - Display loading state while fetching
    - Display empty state with helpful message when no classrooms
    - Render classroom cards in grid layout
    - Show name, description (truncated), URL count, creation date for each
    - Add "Create Classroom" button
    - Handle navigation to classroom detail page on card click
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_
  
  - [x] 9.2 Create classroom card component
    - Display classroom name as heading
    - Display description (truncated to 100 chars)
    - Display URL count badge
    - Display formatted creation date
    - Add hover effects and click handler
    - Use teacher-friendly styling (clear, non-technical)
    - _Requirements: 2.3, 2.5, 5.1, 5.3_

- [x] 10. Implement frontend create classroom functionality
  - [x] 10.1 Create classroom creation form component
    - Add name input field (required, max 100 chars)
    - Add description textarea (optional, max 500 chars)
    - Add character count indicators
    - Implement client-side validation
    - Add submit button with loading state
    - Display validation errors inline
    - Display success/error toast notifications
    - Clear form on successful creation
    - _Requirements: 1.1, 1.3, 1.4, 5.2, 5.3_
  
  - [x] 10.2 Integrate form with classroom list page
    - Add form as modal or expandable section on list page
    - Update classroom list immediately after creation (no refresh)
    - Handle form submission and API errors
    - _Requirements: 1.6, 5.2_

- [x] 11. Implement frontend classroom detail page
  - [x] 11.1 Create /app/classrooms/[id]/page.tsx for classroom details
    - Fetch classroom detail on component mount
    - Display loading state while fetching
    - Display classroom name and description in header
    - Display back button to return to list
    - Handle 404 error if classroom not found
    - _Requirements: 3.1, 3.2, 3.5_
  
  - [x] 11.2 Create URL list component for classroom detail
    - Display all URLs in list format
    - Show URL, creation date for each URL
    - Display empty state when no URLs exist
    - Use clear, readable styling
    - _Requirements: 3.3, 3.4, 5.1, 5.3_
  
  - [x] 11.3 Create add URL form component
    - Add URL input field (required)
    - Add submit button with loading state
    - Implement client-side validation (not empty)
    - Display validation errors inline
    - Display success/error toast notifications
    - Clear input on successful addition
    - Update URL list immediately after addition (no refresh)
    - _Requirements: 4.1, 4.2, 4.4, 4.7, 5.2_

- [x] 12. Implement frontend styling and UI polish
  - [x] 12.1 Apply consistent styling across all components
    - Use consistent color scheme (teacher-friendly, professional)
    - Apply consistent typography (readable, clear)
    - Add icons alongside text for visual clarity
    - Ensure responsive design (desktop, tablet, mobile)
    - Add loading spinners and skeleton screens
    - _Requirements: 5.1, 5.3, 5.4, 5.5, 5.6_
  
  - [x] 12.2 Implement error handling and user feedback
    - Add toast notification system for success/error messages
    - Display user-friendly error messages (translate backend errors)
    - Add retry mechanisms for failed operations
    - Preserve form data when validation fails
    - Clear error states when user corrects input
    - _Requirements: 5.2_
    - Display loading state while fetching
    - Display classroom name and description in header
    - Display back button to return to list
    - Handle 404 error if classroom not found
    - _Requirements: 3.1, 3.2, 3.5_
  
  - [x] 11.2 Create URL list component for classroom detail
    - Display all URLs in list format
    - Show URL, creation date for each URL
    - Display empty state when no URLs exist
    - Use clear, readable styling
    - _Requirements: 3.3, 3.4, 5.1, 5.3_
  
  - [x] 11.3 Create add URL form component
    - Add URL input field (required)
    - Add submit button with loading state
    - Implement client-side validation (not empty)
    - Display validation errors inline
    - Display success/error toast notifications
    - Clear input on successful addition
    - Update URL list immediately after addition (no refresh)
    - _Requirements: 4.1, 4.2, 4.4, 4.7, 5.2_

- [ ] 12. Implement frontend styling and UI polish
  - [x] 12.1 Apply consistent styling across all components
    - Use consistent color scheme (teacher-friendly, professional)
    - Apply consistent typography (readable, clear)
    - Add icons alongside text for visual clarity
    - Ensure responsive design (desktop, tablet, mobile)
    - Add loading spinners and skeleton screens
    - _Requirements: 5.1, 5.3, 5.4, 5.5, 5.6_
  
  - [x] 12.2 Implement error handling and user feedback
    - Add toast notification system for success/error messages
    - Display user-friendly error messages (translate backend errors)
    - Add retry mechanisms for failed operations
    - Preserve form data when validation fails
    - Clear error states when user corrects input
    - _Requirements: 5.2_

- [ ]* 13. Write frontend property tests for UI updates
  - [ ] 13.1 Write property test for Property 13: UI Classroom List Update
    - **Property 13: UI Classroom List Update**
    - **Validates: Requirements 1.6**
    - Create classroom via API, verify appears in frontend list without refresh
  
  - [ ] 13.2 Write property test for Property 14: UI URL List Display
    - **Property 14: UI URL List Display**
    - **Validates: Requirements 3.3**
    - Create classroom with URLs, verify all URLs displayed in frontend
  
  - [ ]* 13.3 Write property test for Property 15: UI URL List Update
    - **Property 15: UI URL List Update**
    - **Validates: Requirements 4.7**
    - Add URL via API, verify appears in frontend list without refresh

- [x] 14. Final checkpoint - Integration testing and verification
  - [x] 14.1 Verify end-to-end flows
    - Test create classroom → appears in list → click to view details
    - Test add URL → appears in classroom detail → verify in database
    - Test all error scenarios (validation, duplicates, not found)
    - Test responsive design on different screen sizes
    - _Requirements: All_
  
  - [x] 14.2 Verify database constraints and behavior
    - Test unique constraint on (classroom_id, url)
    - Test cascade delete (delete classroom deletes URLs)
    - Test foreign key constraints
    - _Requirements: 4.5, 8.3_
  
  - [x] 14.3 Run all tests and verify TypeScript compilation
    - Run backend unit tests: `cd backend && ./mvnw test`
    - Run backend property tests (if implemented)
    - Run frontend type check: `cd frontend && npx tsc --noEmit`
    - Verify no compilation errors or test failures
    - _Requirements: All_
  
  - [x] 14.4 Final checkpoint - Run E2E tests
    - Run E2E tests: `bash test-e2e.sh`
    - Ensure all E2E tests pass before marking spec complete

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Property tests validate universal correctness properties (15 total)
- Unit tests validate specific examples and edge cases
- Backend uses EntityManager with @Transactional for all write operations
- Frontend uses React hooks (useState, useEffect) for state management
- All API calls include proper error handling and loading states
- Database schema supports future features (url_type, sessions, audit logging)
