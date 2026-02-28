# Testing Setup Complete

## Configuration

### Backend Testing
- **Test Database:** H2 in-memory (no Docker required for tests)
- **Dev Mode Database:** PostgreSQL via Quarkus Dev Services (automatic Docker container)
- **Test Configuration:** `backend/src/test/resources/application.properties`
- **Dev Configuration:** `backend/src/main/resources/application.properties`

### Database Modes

#### Dev Mode (`./mvnw quarkus:dev`)
- **Automatic PostgreSQL container** via Quarkus Dev Services
- No manual database setup needed
- Container starts automatically when you run `quarkus:dev`
- Uses postgres:15-alpine image
- Data is ephemeral (resets on restart)

#### Test Mode (`./mvnw test`)
- **H2 in-memory database** (no Docker)
- Fast startup and execution
- Automatic cleanup between tests
- No external dependencies

#### Production Mode
- **External PostgreSQL** at localhost:5432
- Configured via environment variables or application.properties
- Persistent data storage

### Why This Setup?

**Dev Mode (Quarkus Dev Services):**
- Automatic PostgreSQL container
- Real database for development
- No manual setup required
- Requires Docker to be running

**Test Mode (H2):**
- No Docker required
- Fast test execution
- Works in CI/CD environments
- Isolated test runs

**Production:**
- External PostgreSQL
- Full control over database
- Persistent storage
- Production-grade setup

## Running the Application

### Dev Mode (with automatic PostgreSQL)
```bash
cd backend
./mvnw quarkus:dev
```
Quarkus will automatically:
1. Start a PostgreSQL container
2. Configure the connection
3. Run database migrations
4. Start the application

### Test Mode (with H2)
```bash
cd backend
./mvnw test              # All tests
./mvnw test -Dtest=MyTest # Specific test
```

### Production Mode
```bash
cd backend
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```
Requires PostgreSQL running at localhost:5432

## Configuration Files

### Main Config (`backend/src/main/resources/application.properties`)
- Dev Services enabled for dev/test modes
- Production config uses external PostgreSQL
- CORS, logging, HTTP settings

### Test Config (`backend/src/test/resources/application.properties`)
- Overrides to use H2 instead of PostgreSQL
- Disables Dev Services for tests
- Fast in-memory database

## Troubleshooting

### Dev mode fails with "Connection refused"
- **Cause:** Docker not running or Dev Services disabled
- **Fix:** Start Docker, or check `quarkus.devservices.enabled=true`

### Tests fail with database connection error
- **Cause:** H2 dependency missing or test config wrong
- **Fix:** Verify H2 in pom.xml and test config exists

### Dev Services not starting
- **Cause:** JDBC URL hardcoded in config
- **Fix:** Remove `quarkus.datasource.jdbc.url` from dev profile
- **Note:** Only set JDBC URL for production (`%prod.`)

### Port 5432 already in use
- **Cause:** Another PostgreSQL instance running
- **Fix:** Stop other PostgreSQL or let Dev Services use random port

## Next Steps

1. Start dev mode: `./mvnw quarkus:dev` (PostgreSQL starts automatically)
2. Run tests: `./mvnw test` (uses H2, no Docker needed)
3. Create specs and implement features
4. Tests run automatically with H2

The testing environment now supports both automatic PostgreSQL for development and fast H2 for testing!
