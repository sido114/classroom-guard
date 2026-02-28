# Database Conventions

## Entity Design (Kotlin + Panache)

### Base Pattern
```kotlin
@Entity
@Table(name = "sessions")
data class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    
    @Column(nullable = false, length = 100)
    var name: String,
    
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false
) : PanacheEntityBase

companion object : PanacheCompanion<Session> {
    fun findActive(): List<Session> = list("isActive", true)
}
```

### Naming Conventions
- Table names: `snake_case`, plural: `sessions`, `focus_sessions`
- Column names: `snake_case`: `created_at`, `is_active`
- Kotlin properties: `camelCase`: `createdAt`, `isActive`
- Use `@Column(name = "...")` to map between them

### Primary Keys
- Always use `Long` with `@GeneratedValue`
- Name it `id` consistently
- Let PostgreSQL handle auto-increment

### Timestamps
```kotlin
@Column(name = "created_at", nullable = false)
var createdAt: LocalDateTime = LocalDateTime.now()

@Column(name = "updated_at")
var updatedAt: LocalDateTime? = null
```

### Relationships

#### One-to-Many
```kotlin
@Entity
class Session : PanacheEntityBase {
    @Id @GeneratedValue
    var id: Long? = null
    
    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL])
    var students: MutableList<Student> = mutableListOf()
}

@Entity
class Student : PanacheEntityBase {
    @Id @GeneratedValue
    var id: Long? = null
    
    @ManyToOne
    @JoinColumn(name = "session_id")
    var session: Session? = null
}
```

#### Many-to-Many (Future)
Use join table when needed.

### Validation
```kotlin
@Column(nullable = false, length = 100)
@NotBlank(message = "Name cannot be empty")
var name: String
```

### Indexes (Future)
```kotlin
@Table(
    name = "sessions",
    indexes = [
        Index(name = "idx_session_active", columnList = "is_active"),
        Index(name = "idx_session_created", columnList = "created_at")
    ]
)
```

## Database Migrations

### Using Flyway (Recommended for Production)
Not set up yet, but when we do:
```sql
-- V001__create_sessions.sql
CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT FALSE
);
```

### Using Hibernate DDL (Current - Dev Only)
```properties
# application.properties
quarkus.hibernate-orm.database.generation=drop-and-create  # Dev only!
quarkus.hibernate-orm.sql-load-script=import.sql           # Seed data
```

### Seed Data (import.sql)
```sql
-- backend/src/main/resources/import.sql
INSERT INTO sessions (name, created_at, is_active) VALUES ('Test Session', NOW(), true);
```

## Querying with Panache

### Simple Queries
```kotlin
// Find all
val all = Session.listAll()

// Find by ID
val session = Session.findById(1L)

// Find by field
val active = Session.list("isActive", true)

// Find with query
val recent = Session.list("createdAt > ?1", yesterday)
```

### Complex Queries
```kotlin
// Named query
val sessions = Session.find(
    "isActive = :active and createdAt > :date",
    Parameters.with("active", true).and("date", yesterday)
).list()

// Count
val count = Session.count("isActive", true)

// Delete
Session.delete("isActive", false)
```

### Transactions
```kotlin
@Transactional
fun createSession(name: String): Session {
    val session = Session(name = name)
    session.persist()
    return session
}
```

## PostgreSQL Configuration

### application.properties
```properties
# Database connection
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=teacher
quarkus.datasource.password=securepassword
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/classroom_db

# Hibernate
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.sql-load-script=import.sql
```

### Dev Services (Automatic Test DB)
Quarkus automatically starts a PostgreSQL container for tests. No config needed!

## Data Privacy (Swiss revDSG Compliance)

### Minimal Data Collection
- Store only what's necessary
- No student names if possible (use device IDs)
- No tracking beyond session scope

### Data Retention
- Delete sessions after 30 days (future feature)
- Anonymize logs
- No persistent IP addresses

### Audit Trail (Future)
```kotlin
@Entity
class AuditLog {
    @Id @GeneratedValue
    var id: Long? = null
    
    var action: String  // "SESSION_CREATED", "STUDENT_JOINED"
    var timestamp: LocalDateTime
    var teacherId: String  // Anonymized
}
```
