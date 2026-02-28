# Database Configuration Modes

## Overview

The project uses different database configurations for different purposes:

| Mode | Database | Docker Required | Use Case |
|------|----------|----------------|----------|
| **Dev** | PostgreSQL (Dev Services) | Yes | Local development with `quarkus:dev` |
| **Test** | H2 In-Memory | No | Running tests with `mvnw test` |
| **Prod** | PostgreSQL (External) | Optional | Production deployment |

## Dev Mode (Quarkus Dev Services)

### What It Does
- Automatically starts a PostgreSQL container when you run `./mvnw quarkus:dev`
- Uses `postgres:15-alpine` image
- Provides a real PostgreSQL database for development
- Data is ephemeral (resets when you stop dev mode)

### Configuration
In `backend/src/main/resources/application.properties`:
```properties
quarkus.datasource.db-kind=postgresql
quarkus.devservices.enabled=true
quarkus.datasource.devservices.enabled=true
quarkus.datasource.devservices.image-name=postgres:15-alpine
```

### How to Use
```bash
cd backend
./mvnw quarkus:dev
```

Quarkus will:
1. Check if Docker is running
2. Start a PostgreSQL container
3. Configure the connection automatically
4. Run your application

### Requirements
- Docker must be running
- No PostgreSQL manually started on port 5432

### Troubleshooting
**Error: "Connection to localhost:5432 refused"**
- Cause: Docker not running or Dev Services disabled
- Fix: Start Docker Desktop/daemon

**Error: "Port 5432 already in use"**
- Cause: Another PostgreSQL instance running
- Fix: Stop other PostgreSQL or change Dev Services port

## Test Mode (H2 In-Memory)

### What It Does
- Uses H2 in-memory database for tests
- No Docker required
- Fast startup and execution
- Each test gets a clean database

### Configuration
In `backend/src/test/resources/application.properties`:
```properties
%test.quarkus.datasource.db-kind=h2
%test.quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
%test.quarkus.devservices.enabled=false
```

### How to Use
```bash
cd backend
./mvnw test                    # All tests
./mvnw test -Dtest=MyTest      # Specific test
```

### Requirements
- H2 JDBC driver in pom.xml (already added)
- No external dependencies

### Why H2 for Tests?
- **Fast:** In-memory, no I/O overhead
- **Isolated:** Each test run is independent
- **CI-Friendly:** Works without Docker
- **Simple:** No setup required

## Production Mode

### What It Does
- Connects to external PostgreSQL database
- Persistent data storage
- Production-grade configuration

### Configuration
In `backend/src/main/resources/application.properties`:
```properties
%prod.quarkus.datasource.username=teacher
%prod.quarkus.datasource.password=securepassword
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/classroom_db
```

### How to Use

**With Docker Compose:**
```bash
docker-compose up -d
```

**Standalone:**
```bash
# Start PostgreSQL manually
# Then run:
cd backend
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Requirements
- PostgreSQL running at localhost:5432
- Database `classroom_db` created
- User `teacher` with password `securepassword`

## Configuration Profiles

Quarkus uses profiles to switch between modes:

| Profile | Activated By | Database |
|---------|-------------|----------|
| `dev` | `./mvnw quarkus:dev` | PostgreSQL (Dev Services) |
| `test` | `./mvnw test` | H2 (overridden in test config) |
| `prod` | `java -jar ...` or Docker | PostgreSQL (external) |

## Key Configuration Properties

### Enable/Disable Dev Services
```properties
# Enable for dev mode (default)
quarkus.devservices.enabled=true

# Disable for tests (use H2 instead)
%test.quarkus.devservices.enabled=false
```

### Database Kind
```properties
# Main config (PostgreSQL)
quarkus.datasource.db-kind=postgresql

# Test override (H2)
%test.quarkus.datasource.db-kind=h2
```

### JDBC URL
```properties
# Don't set for dev mode (let Dev Services handle it)
# Only set for production
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/classroom_db
```

## Common Issues

### Issue: Dev mode can't connect to database
**Symptoms:** `Connection to localhost:5432 refused`
**Cause:** JDBC URL hardcoded in config, preventing Dev Services
**Fix:** Remove `quarkus.datasource.jdbc.url` from dev profile

### Issue: Tests fail with PostgreSQL connection error
**Symptoms:** Tests try to connect to PostgreSQL
**Cause:** Test config not overriding to H2
**Fix:** Verify `backend/src/test/resources/application.properties` exists

### Issue: Dev Services not starting
**Symptoms:** No Docker container starts
**Cause:** Docker not running or Dev Services disabled
**Fix:** Start Docker and check `quarkus.devservices.enabled=true`

## Best Practices

1. **Never hardcode JDBC URL in dev profile** - Let Dev Services manage it
2. **Use H2 for tests** - Fast and no external dependencies
3. **Use environment variables in production** - Don't commit credentials
4. **Keep Dev Services enabled** - Automatic database management
5. **Test with H2, develop with PostgreSQL** - Best of both worlds

## Summary

- **Dev Mode:** Automatic PostgreSQL via Dev Services (requires Docker)
- **Test Mode:** H2 in-memory (no Docker, fast)
- **Production:** External PostgreSQL (managed separately)

This setup gives you:
- ✓ Automatic database for development
- ✓ Fast tests without Docker
- ✓ Production-ready configuration
- ✓ No manual database setup for dev/test
