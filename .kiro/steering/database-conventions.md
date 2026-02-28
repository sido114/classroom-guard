# Database Conventions

## Entity Design (Kotlin + JPA)

### Base Pattern (Plain JPA - What Actually Works)
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

### Resource Pattern (Using EntityManager)
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
        val entity = SavedUrl()
        entity.url = request.url
        em.persist(entity)
        return Response.status(201).entity(entity).build()
    }
}
```

### Naming Conventions
- Table names: `snake_case`, plural: `saved_urls`, `sessions`
- Column names: `snake_case`: `created_at`, `is_active`
- Kotlin properties: `camelCase`: `createdAt`, `isActive`
- Use `@Column(name = "...")` to map between them

### Primary Keys
- Always use `Long?` with `@GeneratedValue`
- Name it `id` consistently
- Let PostgreSQL handle auto-increment

### Timestamps
```kotlin
@Column(name = "created_at", nullable = false)
var createdAt: LocalDateTime = LocalDateTime.now()

@Column(name = "updated_at")
var updatedAt: LocalDateTime? = null
```

### Validation
```kotlin
@Column(nullable = false, length = 100)
var name: String = ""

// In resource, validate before persist:
if (request.name.isBlank()) {
    return Response.status(400)
        .entity(mapOf("error" to "Name cannot be empty"))
        .build()
}
```

## Database Configuration

### application.properties
```properties
# Database - Dev Services (automatic PostgreSQL for dev/test)
quarkus.datasource.db-kind=postgresql
quarkus.devservices.enabled=true

# Production database
%prod.quarkus.datasource.username=teacher
%prod.quarkus.datasource.password=securepassword
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/classroom_db

# Hibernate
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=true
```

### Dev Services (Automatic Test DB)
Quarkus automatically starts a PostgreSQL container for dev/test. No manual setup needed!

## Querying with EntityManager

### Simple Queries
```kotlin
// Find all
val all = em.createQuery("SELECT e FROM Entity e", Entity::class.java).resultList

// Find by ID
val entity = em.find(Entity::class.java, id)

// Find with condition
val results = em.createQuery(
    "SELECT e FROM Entity e WHERE e.active = :active", 
    Entity::class.java
).setParameter("active", true).resultList
```

### Transactions
```kotlin
@POST
@Transactional
fun create(request: CreateRequest): Response {
    val entity = Entity()
    entity.field = request.field
    em.persist(entity)
    return Response.status(201).entity(entity).build()
}
```

## Data Privacy (Swiss revDSG Compliance)

### Minimal Data Collection
- Store only what's necessary
- No student names if possible (use device IDs)
- No tracking beyond session scope

### Data Retention
- Delete old data after 30 days (future feature)
- Anonymize logs
- No persistent IP addresses
