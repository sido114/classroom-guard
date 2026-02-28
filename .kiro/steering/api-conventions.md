# API Conventions

## REST Endpoint Standards

### URL Structure
```
/api/{resource}/{id?}/{sub-resource?}
```

Examples:
- `GET /api/sessions` - List all sessions
- `GET /api/sessions/{id}` - Get specific session
- `POST /api/sessions` - Create session
- `PATCH /api/sessions/{id}` - Update session
- `DELETE /api/sessions/{id}` - Delete session
- `POST /api/sessions/{id}/students` - Add student to session

### HTTP Methods
- `GET` - Read, idempotent, no body
- `POST` - Create, not idempotent, has body
- `PATCH` - Partial update, has body
- `PUT` - Full replacement (use sparingly)
- `DELETE` - Remove, idempotent

### Response Codes
- `200 OK` - Success with body
- `201 Created` - Resource created, return Location header
- `204 No Content` - Success, no body needed
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource doesn't exist
- `500 Internal Server Error` - Server error

### Request/Response Format
Always JSON, always UTF-8.

#### Success Response
```json
{
  "id": "123",
  "name": "Math Class",
  "createdAt": "2026-02-28T10:00:00Z"
}
```

#### Error Response
```json
{
  "error": "Validation failed",
  "message": "Session name cannot be empty",
  "field": "name"
}
```

### Naming Conventions
- Use plural nouns: `/api/sessions`, not `/api/session`
- Use kebab-case for multi-word: `/api/focus-sessions`
- Use camelCase in JSON: `createdAt`, not `created_at`
- Be consistent across all endpoints

### Pagination (Future)
```
GET /api/sessions?page=1&size=20
```

Response:
```json
{
  "data": [...],
  "page": 1,
  "size": 20,
  "total": 150
}
```

### Filtering (Future)
```
GET /api/sessions?status=active&teacherId=123
```

## Kotlin Implementation Pattern

```kotlin
@Path("/api/sessions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SessionResource {

    @GET
    fun list(): List<Session> {
        return Session.listAll()
    }

    @GET
    @Path("/{id}")
    fun get(@PathParam("id") id: Long): Response {
        val session = Session.findById(id)
            ?: return Response.status(404).build()
        return Response.ok(session).build()
    }

    @POST
    @Transactional
    fun create(session: Session): Response {
        session.persist()
        return Response.status(201)
            .entity(session)
            .build()
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    fun update(@PathParam("id") id: Long, updates: SessionUpdate): Response {
        val session = Session.findById(id)
            ?: return Response.status(404).build()
        
        updates.name?.let { session.name = it }
        session.persist()
        
        return Response.ok(session).build()
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = Session.deleteById(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(404).build()
        }
    }
}
```

## Frontend API Client Pattern

```typescript
// lib/api.ts
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export async function getSessions() {
  const res = await fetch(`${API_URL}/api/sessions`)
  if (!res.ok) throw new Error('Failed to fetch sessions')
  return res.json()
}

export async function createSession(name: string) {
  const res = await fetch(`${API_URL}/api/sessions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name })
  })
  if (!res.ok) throw new Error('Failed to create session')
  return res.json()
}
```

## CORS Configuration
Backend must allow frontend origin:
```properties
# application.properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:3000,http://frontend:3000
quarkus.http.cors.methods=GET,POST,PATCH,DELETE
```

## Security (Future)
- Add JWT authentication
- Validate teacher permissions
- Rate limiting on public endpoints
- Input sanitization always
