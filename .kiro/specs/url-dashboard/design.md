# Design Document: URL Dashboard

## Overview

Simple MVP to test full-stack integration: Frontend → Backend → PostgreSQL. User can save URLs and see them in a list. That's it.

## Architecture

```
Frontend (Next.js) → Backend (Quarkus) → PostgreSQL
     page.tsx      →   UrlResource    →  saved_urls table
                        SavedUrl entity
```

**API Endpoints**:
- `GET /api/urls` - List all URLs
- `POST /api/urls` - Save a URL

## Implementation

### Backend (3 files)

**SavedUrl.kt** - Entity
```kotlin
@Entity
@Table(name = "saved_urls")
data class SavedUrl(
    @Id @GeneratedValue
    var id: Long? = null,
    
    @Column(nullable = false)
    var url: String,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) : PanacheEntityBase
```

**UrlResource.kt** - REST API
```kotlin
@Path("/api/urls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UrlResource {
    @GET
    fun list(): List<SavedUrl> = SavedUrl.listAll()

    @POST
    @Transactional
    fun create(request: Map<String, String>): Response {
        val url = request["url"] ?: return Response.status(400).build()
        if (url.isBlank()) return Response.status(400).build()
        
        val saved = SavedUrl(url = url)
        saved.persist()
        return Response.status(201).entity(saved).build()
    }
}
```

**UrlResourceTest.kt** - Tests
```kotlin
@QuarkusTest
class UrlResourceTest {
    @Test
    fun `GET returns 200`() {
        given().`when`().get("/api/urls").then().statusCode(200)
    }
    
    @Test
    fun `POST with valid URL returns 201`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com"}""")
        .`when`().post("/api/urls")
        .then().statusCode(201)
    }
}
```

### Frontend (1 file)

**app/page.tsx** - Everything in one component
```typescript
'use client'
import { useState, useEffect } from 'react'

const API = 'http://localhost:8080/api/urls'

export default function Home() {
  const [urls, setUrls] = useState([])
  const [input, setInput] = useState('')

  const load = async () => {
    const res = await fetch(API)
    setUrls(await res.json())
  }

  useEffect(() => { load() }, [])

  const save = async (e) => {
    e.preventDefault()
    await fetch(API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ url: input })
    })
    setInput('')
    load()
  }

  return (
    <div>
      <h1>URL Dashboard</h1>
      <form onSubmit={save}>
        <input value={input} onChange={e => setInput(e.target.value)} />
        <button>Save</button>
        <button type="button" onClick={load}>Reload</button>
      </form>
      <ul>
        {urls.map(u => <li key={u.id}>{u.url}</li>)}
      </ul>
    </div>
  )
}
```

### Database

Table created automatically by Hibernate from entity.
## Testing

**Backend**: Run `./mvnw test` - must pass
**Frontend**: Run `npx tsc --noEmit` and `npm run build` - must pass
**Integration**: Start `docker-compose up` and manually test saving/loading URLs

