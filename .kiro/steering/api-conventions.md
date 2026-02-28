# API Conventions

## REST Endpoint Standards

### URL Structure
```
/api/{resource}/{id?}/{sub-resource?}
```

Examples:
- `GET /api/urls` - List all URLs
- `GET /api/urls/{id}` - Get specific URL
- `POST /api/urls` - Create URL
- `DELETE /api/urls/{id}` - Delete URL

### HTTP Methods
- `GET` - Read, idempotent, no body
- `POST` - Create, not idempotent, has body
- `PATCH` - Partial update, has body
- `DELETE` - Remove, idempotent

### Response Codes
- `200 OK` - Success with body
- `201 Created` - Resource created
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource doesn't exist
- `500 Internal Server Error` - Server error

### Request/Response Format
Always JSON, always UTF-8.

#### Success Response
```json
{
  "id": 1,
  "url": "https://example.com",
  "createdAt": "2026-02-28T10:00:00"
}
```

#### Error Response
```json
{
  "error": "URL cannot be empty"
}
```

### Naming Conventions
- Use plural nouns: `/api/urls`, not `/api/url`
- Use camelCase in JSON: `createdAt`, not `created_at`

## Kotlin Implementation Pattern

```kotlin
@Path("/api/urls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SavedUrlResource {

    @Inject
    lateinit var em: EntityManager

    @GET
    fun list(): List<SavedUrl> {
        return em.createQuery("SELECT s FROM SavedUrl s", SavedUrl::class.java).resultList
    }

    @POST
    @Transactional
    fun create(request: CreateUrlRequest): Response {
        // Validate
        if (request.url.isBlank()) {
            return Response.status(400)
                .entity(mapOf("error" to "URL cannot be empty"))
                .build()
        }
        
        // Create
        val savedUrl = SavedUrl()
        savedUrl.url = request.url
        em.persist(savedUrl)
        
        return Response.status(201).entity(savedUrl).build()
    }
}

data class CreateUrlRequest(
    val url: String
)
```

## CORS Configuration

### Method 1: CorsFilter (Recommended)
Create a JAX-RS filter for reliable CORS handling:

```kotlin
@Provider
class CorsFilter : ContainerResponseFilter {
    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        responseContext.headers.add("Access-Control-Allow-Origin", "*")
        responseContext.headers.add("Access-Control-Allow-Credentials", "true")
        responseContext.headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
        responseContext.headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH")
    }
}
```

### Method 2: application.properties (Backup)
```properties
quarkus.http.cors=true
quarkus.http.cors.origins=*
```

## Frontend API Client Pattern

```typescript
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

interface SavedUrl {
  id: number
  url: string
  createdAt: string
}

async function fetchUrls(): Promise<SavedUrl[]> {
  const res = await fetch(`${API_URL}/api/urls`)
  if (!res.ok) throw new Error('Failed to fetch')
  return res.json()
}

async function createUrl(url: string): Promise<SavedUrl> {
  const res = await fetch(`${API_URL}/api/urls`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url })
  })
  if (!res.ok) throw new Error('Failed to create')
  return res.json()
}
```
