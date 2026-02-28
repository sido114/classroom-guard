# Kiro's Responsibilities

## Spec-Driven Development

When working on a spec (in `.kiro/specs/feature-name/`):

### 1. Read the Spec Files
- `requirements.md` or `bugfix.md` - What we're building
- `design.md` - How we're building it
- `tasks.md` - Step-by-step implementation plan

### 2. Implement Each Task
- Follow conventions from steering files
- Write minimal, focused code
- Create tests alongside implementation
- Update task status as you go

### 3. Follow Conventions
- **API**: Use patterns from `api-conventions.md`
- **Database**: Use patterns from `database-conventions.md`
- **Testing**: Use patterns from `testing-standards.md`
- **Kotlin**: Idiomatic Kotlin, clean code

### 4. Test Everything
After implementation:
- Backend: Check diagnostics, run tests if possible
- Frontend: Check TypeScript compilation
- Report any errors immediately

### 5. Keep It Minimal
- Only implement what the task requires
- No extra features
- No over-engineering
- Focus on the requirements

## Code Quality Standards

### Backend (Kotlin)
- Use EntityManager for database operations
- Add `@Transactional` for write operations
- Validate input before persisting
- Return appropriate HTTP status codes
- Handle errors gracefully

### Frontend (TypeScript)
- Define interfaces for all data types
- Use environment variables for API URLs
- Handle loading and error states
- Type-safe fetch operations

### Testing
- Every endpoint needs tests
- Test happy path and error cases
- Tests should be isolated and fast
- Use H2 for backend tests (automatic)

## Common Patterns

### Backend Entity
```kotlin
@Entity
@Table(name = "saved_urls")
class SavedUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    @Column(nullable = false)
    var url: String = ""
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
```

### Backend Resource
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
        if (request.url.isBlank()) {
            return Response.status(400)
                .entity(mapOf("error" to "URL cannot be empty"))
                .build()
        }
        val entity = SavedUrl()
        entity.url = request.url
        em.persist(entity)
        return Response.status(201).entity(entity).build()
    }
}
```

### Backend Test
```kotlin
@QuarkusTest
class SavedUrlResourceTest {
    @Test
    fun `should return empty list initially`() {
        given()
            .`when`().get("/api/urls")
            .then()
            .statusCode(200)
            .body("size()", `is`(0))
    }
    
    @Test
    fun `should create a new URL`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com"}""")
            .`when`().post("/api/urls")
            .then()
            .statusCode(201)
            .body("url", `is`("https://example.com"))
    }
    
    @Test
    fun `should return 400 when URL is empty`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": ""}""")
            .`when`().post("/api/urls")
            .then()
            .statusCode(400)
    }
}
```

### Frontend Component
```typescript
'use client'
import { useEffect, useState } from 'react'

interface SavedUrl {
  id: number
  url: string
  createdAt: string
}

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export default function Dashboard() {
  const [urls, setUrls] = useState<SavedUrl[]>([])
  const [inputUrl, setInputUrl] = useState('')
  const [loading, setLoading] = useState(false)

  const fetchUrls = async () => {
    const res = await fetch(`${API_URL}/api/urls`)
    if (res.ok) {
      setUrls(await res.json())
    }
  }

  useEffect(() => {
    fetchUrls()
  }, [])

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!inputUrl.trim()) return
    
    setLoading(true)
    const res = await fetch(`${API_URL}/api/urls`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ url: inputUrl })
    })
    
    if (res.ok) {
      setInputUrl('')
      await fetchUrls()
    }
    setLoading(false)
  }

  return (
    <main>
      <form onSubmit={handleSave}>
        <input
          value={inputUrl}
          onChange={(e) => setInputUrl(e.target.value)}
          disabled={loading}
        />
        <button type="submit" disabled={loading}>
          Save
        </button>
      </form>
      
      {urls.length === 0 ? (
        <p>No URLs saved yet</p>
      ) : (
        <ul>
          {urls.map((item) => (
            <li key={item.id}>{item.url}</li>
          ))}
        </ul>
      )}
    </main>
  )
}
```

## Error Handling

### When Tests Fail
1. Read the error message
2. Identify the root cause
3. Fix the code
4. Re-run tests
5. Report results

### When Requirements Unclear
1. Ask for clarification
2. Don't guess
3. Suggest improvements to the spec

## Communication

- Be concise
- Report progress clearly
- Explain failures
- Suggest next steps
- No unnecessary verbosity
