# Design Document: Classroom URL Management

## Overview

The Classroom URL Management feature enables teachers to create classrooms and manage URL lists for each classroom. This is the foundational feature for future DNS-based internet filtering in Classroom Guard. The design follows a simple add-only approach, focusing on core functionality without edit or delete operations in this iteration.

### Key Design Goals

1. **Simplicity First**: Non-technical teachers should find the interface intuitive and straightforward
2. **Future-Ready**: Database schema and API design support future NextDNS integration
3. **Reliability**: All data operations are transactional and error-handled
4. **Performance**: Efficient queries and minimal database roundtrips
5. **Swiss Compliance**: Data structure supports future revDSG compliance requirements

### Design Principles

- Use established patterns from existing SavedUrl implementation
- Follow Kotlin + Quarkus + EntityManager conventions
- Maintain RESTful API design with clear resource boundaries
- Keep frontend components simple and focused
- Prepare for future session-based filtering without over-engineering

## Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (Next.js)                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Classroom    │  │ Classroom    │  │ Classroom    │      │
│  │ List Page    │  │ Detail Page  │  │ Create Form  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            │ HTTP/JSON
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Backend (Quarkus + Kotlin)                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              ClassroomResource (REST API)            │   │
│  │  - POST   /api/classrooms                            │   │
│  │  - GET    /api/classrooms                            │   │
│  │  - GET    /api/classrooms/{id}                       │   │
│  │  - POST   /api/classrooms/{id}/urls                  │   │
│  └──────────────────────────────────────────────────────┘   │
│                            │                                 │
│                            ▼                                 │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         EntityManager (JPA)                          │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                       │
│  ┌──────────────┐              ┌──────────────┐            │
│  │  classrooms  │──────────────│classroom_urls│            │
│  │              │  1        *  │              │            │
│  └──────────────┘              └──────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **Create Classroom Flow**:
   - Teacher fills form → Frontend validates → POST /api/classrooms → Backend validates → EntityManager persists → Return 201 with classroom data

2. **List Classrooms Flow**:
   - Teacher visits page → GET /api/classrooms → Backend queries with URL count → Return 200 with classroom list

3. **View Classroom Details Flow**:
   - Teacher clicks classroom → GET /api/classrooms/{id} → Backend queries classroom + URLs → Return 200 with full details

4. **Add URL Flow**:
   - Teacher submits URL → Frontend validates → POST /api/classrooms/{id}/urls → Backend validates + normalizes → Check duplicates → EntityManager persists → Return 201 with URL data

## Components and Interfaces

### Backend Components

#### 1. Classroom Entity

```kotlin
@Entity
@Table(name = "classrooms")
class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @Column(nullable = false, length = 100)
    var name: String = ""
    
    @Column(length = 500)
    var description: String? = null
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
    
    @OneToMany(mappedBy = "classroom", cascade = [CascadeType.ALL], orphanRemoval = true)
    var urls: MutableList<ClassroomUrl> = mutableListOf()
}
```

#### 2. ClassroomUrl Entity

```kotlin
@Entity
@Table(
    name = "classroom_urls",
    uniqueConstraints = [UniqueConstraint(columnNames = ["classroom_id", "url"])]
)
class ClassroomUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    var classroom: Classroom? = null
    
    @Column(nullable = false, length = 2048)
    var url: String = ""
    
    @Column(name = "url_type", length = 20)
    var urlType: String = "whitelist"  // Future: "whitelist" or "blacklist"
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
```

#### 3. ClassroomResource (REST API)

```kotlin
@Path("/api/classrooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ClassroomResource {
    
    @Inject
    lateinit var em: EntityManager
    
    @GET
    fun listClassrooms(): List<ClassroomListResponse>
    
    @GET
    @Path("/{id}")
    fun getClassroom(@PathParam("id") id: Long): Response
    
    @POST
    @Transactional
    fun createClassroom(request: CreateClassroomRequest): Response
    
    @POST
    @Path("/{id}/urls")
    @Transactional
    fun addUrl(@PathParam("id") id: Long, request: AddUrlRequest): Response
}
```

#### 4. Request/Response DTOs

```kotlin
data class CreateClassroomRequest(
    val name: String,
    val description: String? = null
)

data class AddUrlRequest(
    val url: String
)

data class ClassroomListResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val urlCount: Long,
    val createdAt: String
)

data class ClassroomDetailResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val urls: List<UrlResponse>,
    val createdAt: String
)

data class UrlResponse(
    val id: Long,
    val url: String,
    val urlType: String,
    val createdAt: String
)
```

#### 5. URL Validation and Normalization

```kotlin
object UrlValidator {
    private val domainPattern = Regex(
        "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"
    )
    
    fun isValid(url: String): Boolean {
        val normalized = normalize(url)
        return try {
            val uri = URI(normalized)
            uri.host != null && domainPattern.matches(uri.host)
        } catch (e: Exception) {
            false
        }
    }
    
    fun normalize(url: String): String {
        var normalized = url.trim().lowercase()
        
        // Add protocol if missing
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://$normalized"
        }
        
        return normalized
    }
    
    fun extractDomain(url: String): String {
        val normalized = normalize(url)
        return try {
            URI(normalized).host ?: url
        } catch (e: Exception) {
            url
        }
    }
}
```

### Frontend Components

#### 1. Page Structure

```
/app
  /classrooms
    page.tsx              # List all classrooms
    /[id]
      page.tsx            # View classroom details
    /new
      page.tsx            # Create new classroom (optional, can be modal)
```

#### 2. TypeScript Interfaces

```typescript
interface Classroom {
  id: number
  name: string
  description: string | null
  urlCount: number
  createdAt: string
}

interface ClassroomDetail {
  id: number
  name: string
  description: string | null
  urls: ClassroomUrl[]
  createdAt: string
}

interface ClassroomUrl {
  id: number
  url: string
  urlType: string
  createdAt: string
}

interface CreateClassroomRequest {
  name: string
  description?: string
}

interface AddUrlRequest {
  url: string
}
```

#### 3. API Client Functions

```typescript
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

async function fetchClassrooms(): Promise<Classroom[]> {
  const res = await fetch(`${API_URL}/api/classrooms`)
  if (!res.ok) throw new Error('Failed to fetch classrooms')
  return res.json()
}

async function fetchClassroomDetail(id: number): Promise<ClassroomDetail> {
  const res = await fetch(`${API_URL}/api/classrooms/${id}`)
  if (!res.ok) throw new Error('Failed to fetch classroom')
  return res.json()
}

async function createClassroom(data: CreateClassroomRequest): Promise<ClassroomDetail> {
  const res = await fetch(`${API_URL}/api/classrooms`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  })
  if (!res.ok) {
    const error = await res.json()
    throw new Error(error.error || 'Failed to create classroom')
  }
  return res.json()
}

async function addUrlToClassroom(classroomId: number, url: string): Promise<ClassroomUrl> {
  const res = await fetch(`${API_URL}/api/classrooms/${classroomId}/urls`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url })
  })
  if (!res.ok) {
    const error = await res.json()
    throw new Error(error.error || 'Failed to add URL')
  }
  return res.json()
}
```

#### 4. Component Design Patterns

**Classroom List Component**:
- Grid or list layout showing classroom cards
- Each card displays: name, description (truncated), URL count, creation date
- "Create Classroom" button prominently displayed
- Empty state with helpful message when no classrooms exist
- Loading state while fetching data

**Classroom Detail Component**:
- Header with classroom name and description
- URL list section with all URLs displayed
- "Add URL" form inline or as expandable section
- Back button to return to classroom list
- Empty state when no URLs exist
- Real-time updates when URLs are added

**Create Classroom Form**:
- Name input (required, max 100 chars)
- Description textarea (optional, max 500 chars)
- Character count indicators
- Submit button with loading state
- Validation feedback
- Success/error messages

## Data Models

### Database Schema

#### classrooms Table

| Column      | Type         | Constraints                    | Description                    |
|-------------|--------------|--------------------------------|--------------------------------|
| id          | BIGSERIAL    | PRIMARY KEY                    | Auto-incrementing identifier   |
| name        | VARCHAR(100) | NOT NULL                       | Classroom name                 |
| description | VARCHAR(500) | NULL                           | Optional description           |
| created_at  | TIMESTAMP    | NOT NULL, DEFAULT NOW()        | Creation timestamp             |

**Indexes**:
- Primary key on `id`
- Index on `created_at` for sorting

#### classroom_urls Table

| Column       | Type          | Constraints                           | Description                    |
|--------------|---------------|---------------------------------------|--------------------------------|
| id           | BIGSERIAL     | PRIMARY KEY                           | Auto-incrementing identifier   |
| classroom_id | BIGINT        | NOT NULL, FOREIGN KEY → classrooms.id | Reference to classroom         |
| url          | VARCHAR(2048) | NOT NULL                              | Normalized URL                 |
| url_type     | VARCHAR(20)   | NOT NULL, DEFAULT 'whitelist'         | Future: whitelist/blacklist    |
| created_at   | TIMESTAMP     | NOT NULL, DEFAULT NOW()               | Creation timestamp             |

**Constraints**:
- Primary key on `id`
- Foreign key `classroom_id` references `classrooms(id)` ON DELETE CASCADE
- Unique constraint on `(classroom_id, url)` to prevent duplicates

**Indexes**:
- Primary key on `id`
- Index on `classroom_id` for joins
- Unique index on `(classroom_id, url)`

### Entity Relationships

```
Classroom (1) ──────< (many) ClassroomUrl
```

- One classroom can have many URLs
- Each URL belongs to exactly one classroom
- Cascade delete: deleting a classroom deletes all its URLs
- Orphan removal: removing a URL from classroom.urls deletes it from database

### Data Validation Rules

**Classroom**:
- `name`: Required, 1-100 characters, trimmed
- `description`: Optional, max 500 characters, trimmed

**ClassroomUrl**:
- `url`: Required, 1-2048 characters, must be valid domain/URL format
- `url` is normalized before storage (lowercase, protocol added if missing)
- `urlType`: Defaults to "whitelist", future values: "whitelist" or "blacklist"

### Future Schema Extensions

The schema is designed to support future features without breaking changes:

1. **URL Type Toggle**: `url_type` column already exists, just needs UI toggle
2. **Session Management**: Can add `sessions` table with `classroom_id` foreign key
3. **Student Devices**: Can add `devices` table linked to sessions
4. **Audit Logging**: Timestamps already in place, can add `updated_at` and `updated_by` columns
5. **Teacher Ownership**: Can add `teacher_id` column to classrooms table


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property Reflection

After analyzing all acceptance criteria, I identified the following redundancies and consolidations:

- **Validation properties**: Criteria 1.3, 1.4, 4.2, 4.4, 6.4, and 6.5 all relate to input validation. These can be consolidated into comprehensive validation properties.
- **Persistence properties**: Criteria 1.2, 4.3, and 8.1 all relate to data persistence. These can be consolidated into round-trip properties.
- **Metadata properties**: Criteria 1.5, 4.8, and 7.3 all relate to metadata (IDs and timestamps). These can be consolidated into a single property.
- **Duplicate prevention**: Criteria 4.5 and 4.6 are the same property expressed differently.
- **URL format properties**: Criteria 6.1, 6.2, and 7.1 all relate to URL format handling and can be consolidated.

### Property 1: Classroom Creation Persistence

*For any* valid classroom name and optional description, creating a classroom and then retrieving it should return a classroom with the same name and description.

**Validates: Requirements 1.2, 8.1**

### Property 2: Classroom Name Validation

*For any* string that is empty or exceeds 100 characters, attempting to create a classroom with that name should return a 400 error.

**Validates: Requirements 1.3, 1.4**

### Property 3: Classroom Metadata Assignment

*For any* successfully created classroom, the classroom should have a non-null unique identifier and a non-null creation timestamp.

**Validates: Requirements 1.5, 7.3**

### Property 4: Classroom List Ordering

*For any* set of classrooms, retrieving the classroom list should return all classrooms ordered by creation date with the newest first.

**Validates: Requirements 2.2**

### Property 5: Classroom List Completeness

*For any* classroom in the database, the classroom list response should include the classroom's name, description, URL count, and creation date.

**Validates: Requirements 2.3**

### Property 6: Classroom Detail Retrieval

*For any* classroom with associated URLs, retrieving the classroom details should return the classroom information including name, description, and all associated URLs.

**Validates: Requirements 3.2**

### Property 7: URL Addition Persistence

*For any* valid URL and existing classroom, adding the URL to the classroom and then retrieving the classroom details should show the URL in the classroom's URL list.

**Validates: Requirements 4.3, 8.1**

### Property 8: URL Validation

*For any* string that is empty or does not contain a valid domain name, attempting to add it as a URL should return a 400 error.

**Validates: Requirements 4.2, 4.4, 6.4, 6.5**

### Property 9: Duplicate URL Prevention

*For any* classroom and URL, if the URL already exists in the classroom, attempting to add the same URL again should return a 400 error indicating the URL already exists.

**Validates: Requirements 4.5, 4.6**

### Property 10: URL Metadata Assignment

*For any* successfully added URL, the URL should have a non-null unique identifier and a non-null creation timestamp.

**Validates: Requirements 4.8, 7.3**

### Property 11: URL Format Acceptance

*For any* URL in formats "example.com", "http://example.com", "https://example.com", or "https://example.com/path", the URL should be accepted and successfully added to a classroom.

**Validates: Requirements 6.1**

### Property 12: URL Normalization Consistency

*For any* two URLs that represent the same domain (e.g., "example.com" and "https://example.com"), they should be normalized to the same format for storage and treated as duplicates.

**Validates: Requirements 6.2, 7.1**

### Property 13: UI Classroom List Update

*For any* valid classroom creation, the frontend classroom list should contain the newly created classroom immediately after creation without requiring a page refresh.

**Validates: Requirements 1.6**

### Property 14: UI URL List Display

*For any* classroom with URLs, the frontend should display all URLs from the classroom in the rendered list.

**Validates: Requirements 3.3**

### Property 15: UI URL List Update

*For any* valid URL addition to a classroom, the frontend URL list should contain the newly added URL immediately after addition without requiring a page refresh.

**Validates: Requirements 4.7**

## Error Handling

### Backend Error Handling Strategy

#### Validation Errors (400 Bad Request)

The backend returns 400 errors with descriptive messages for:

1. **Empty or invalid classroom name**:
   ```json
   { "error": "Classroom name cannot be empty" }
   { "error": "Classroom name cannot exceed 100 characters" }
   ```

2. **Empty or invalid URL**:
   ```json
   { "error": "URL cannot be empty" }
   { "error": "Invalid URL format. Please provide a valid domain (e.g., example.com)" }
   ```

3. **Duplicate URL**:
   ```json
   { "error": "This URL already exists in the classroom" }
   ```

#### Not Found Errors (404 Not Found)

```json
{ "error": "Classroom not found" }
```

Returned when:
- Attempting to retrieve a classroom that doesn't exist
- Attempting to add a URL to a non-existent classroom

#### Server Errors (500 Internal Server Error)

```json
{ "error": "An error occurred while processing your request" }
```

Returned when:
- Database connection fails
- Unexpected exceptions occur
- Transaction rollback occurs

All 500 errors are logged with full stack traces for debugging.

### Frontend Error Handling Strategy

#### User-Facing Error Messages

The frontend translates backend errors into user-friendly messages:

- **Network errors**: "Unable to connect to the server. Please check your internet connection."
- **Validation errors**: Display the backend error message directly (already user-friendly)
- **Server errors**: "Something went wrong. Please try again later."
- **Not found errors**: "The classroom you're looking for doesn't exist."

#### Error Display Patterns

1. **Inline form errors**: Display validation errors below the relevant input field
2. **Toast notifications**: Show success/error messages for actions (create, add)
3. **Error boundaries**: Catch React errors and display fallback UI
4. **Loading states**: Prevent duplicate submissions and show progress

#### Error Recovery

- **Retry mechanism**: Allow users to retry failed operations
- **Form preservation**: Keep form data when validation fails
- **Clear error states**: Remove error messages when user corrects input

### Transaction Management

All write operations use `@Transactional` annotation:

```kotlin
@POST
@Transactional
fun createClassroom(request: CreateClassroomRequest): Response {
    // Validation
    // Entity creation
    // Persistence
    // Return response
}
```

Benefits:
- Automatic rollback on exceptions
- Data consistency guaranteed
- No partial writes to database

### Logging Strategy

```kotlin
private val logger = Logger.getLogger(ClassroomResource::class.java)

// Log all validation failures
logger.warn("Validation failed: ${errorMessage}")

// Log all database errors
logger.error("Database error", exception)

// Log successful operations (debug level)
logger.debug("Created classroom: ${classroom.id}")
```

## Testing Strategy

### Dual Testing Approach

This feature requires both unit tests and property-based tests for comprehensive coverage:

- **Unit tests**: Verify specific examples, edge cases, and integration points
- **Property tests**: Verify universal properties across all inputs

### Property-Based Testing Configuration

**Library**: Kotest Property Testing for Kotlin

**Configuration**:
- Minimum 100 iterations per property test
- Each test tagged with feature name and property reference
- Tag format: `Feature: classroom-url-management, Property {number}: {property_text}`

**Example Property Test Structure**:

```kotlin
class ClassroomPropertyTest : FunSpec({
    
    test("Property 1: Classroom Creation Persistence")
        .config(tags = setOf(Tag("Feature: classroom-url-management"))) {
        
        checkAll(100, Arb.string(1..100)) { name ->
            // Create classroom with random name
            val created = createClassroom(name)
            
            // Retrieve classroom
            val retrieved = getClassroom(created.id)
            
            // Verify persistence
            retrieved.name shouldBe name
        }
    }
})
```

### Unit Testing Strategy

**Backend Unit Tests** (`@QuarkusTest`):

1. **Classroom Creation Tests**:
   - Happy path: valid name and description
   - Edge case: name at exactly 100 characters
   - Error case: empty name
   - Error case: name exceeding 100 characters
   - Edge case: null description

2. **Classroom List Tests**:
   - Empty list when no classrooms exist
   - Single classroom in list
   - Multiple classrooms ordered correctly
   - URL count accuracy

3. **Classroom Detail Tests**:
   - Classroom with no URLs
   - Classroom with multiple URLs
   - Non-existent classroom returns 404

4. **URL Addition Tests**:
   - Happy path: valid URL formats
   - Error case: empty URL
   - Error case: invalid URL format
   - Error case: duplicate URL
   - Edge case: URL at maximum length (2048 chars)
   - Non-existent classroom returns 404

5. **URL Normalization Tests**:
   - "example.com" → "https://example.com"
   - "HTTP://EXAMPLE.COM" → "https://example.com"
   - "https://example.com/path" → "https://example.com/path"

**Frontend Unit Tests** (Jest + React Testing Library):

1. **Classroom List Component**:
   - Renders empty state when no classrooms
   - Renders classroom cards with correct data
   - Navigates to detail page on click
   - Shows loading state while fetching

2. **Classroom Detail Component**:
   - Renders classroom information
   - Renders URL list
   - Renders empty state when no URLs
   - Adds URL successfully
   - Shows validation errors

3. **Create Classroom Form**:
   - Validates required fields
   - Shows character count
   - Submits valid data
   - Shows error messages
   - Clears form on success

### Integration Testing

**Full Stack Tests**:
1. Create classroom via API → Verify in database
2. Add URL via API → Verify in database and in classroom detail response
3. Create classroom via frontend → Verify appears in list
4. Add URL via frontend → Verify appears in classroom detail

### Test Coverage Goals

- **Backend business logic**: 80%+ coverage
- **Frontend critical flows**: Focus on user interactions
- **Property tests**: All 15 properties implemented
- **Unit tests**: All edge cases and error conditions

### Testing Checklist

Before marking implementation complete:

- [ ] All 15 property tests implemented and passing (100+ iterations each)
- [ ] All backend unit tests passing
- [ ] All frontend component tests passing
- [ ] Integration tests passing
- [ ] Manual testing of UI flows completed
- [ ] Error handling verified for all failure scenarios
- [ ] Database constraints verified (unique, foreign key, cascade)
- [ ] CORS configuration verified
- [ ] TypeScript compilation successful with no errors

