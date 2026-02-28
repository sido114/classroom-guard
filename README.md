# Classroom Guard

A classroom management tool for Swiss schools that allows teachers to control iPad internet access in real-time during lessons.

## Tech Stack

- **Backend:** Kotlin + Quarkus (Maven)
- **Frontend:** TypeScript + Next.js (App Router)
- **Database:** PostgreSQL
- **Architecture:** Monorepo with Docker Compose orchestration

## Prerequisites

- **Java 21+** (for Quarkus backend)
- **Node.js 20+** (for Next.js frontend)
- **Docker & Docker Compose** (for full stack deployment)
- **Maven** (included via `mvnw` wrapper)

## Project Structure

```
classroom-guard/
├── backend/              # Kotlin + Quarkus REST API
│   ├── src/
│   │   ├── main/kotlin/  # Application code
│   │   └── test/kotlin/  # Tests
│   └── pom.xml           # Maven dependencies
├── frontend/             # Next.js TypeScript app
│   ├── src/
│   │   └── app/          # App Router pages
│   └── package.json      # npm dependencies
├── .kiro/                # Development workflow
│   ├── specs/            # Feature specifications
│   ├── steering/         # Project guidelines
│   └── hooks/            # Automated testing hooks
└── docker-compose.yml    # Full stack orchestration
```

## Quick Start

### Option 1: Docker Compose (Recommended)

Start the entire stack (backend + frontend + database):

```bash
docker-compose up --build
```

Access:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Database: localhost:5432

Stop everything:
```bash
docker-compose down
```

### Option 2: Local Development

#### Backend (Quarkus)

```bash
cd backend

# Run in dev mode (with live reload)
./mvnw quarkus:dev

# Run tests
./mvnw test

# Build
./mvnw clean package
```

Backend runs at http://localhost:8080

**Note:** Quarkus Dev Services automatically starts a PostgreSQL container for development. No manual database setup needed!

#### Frontend (Next.js)

```bash
cd frontend

# Install dependencies
npm install

# Run dev server
npm run dev

# Type check
npx tsc --noEmit

# Lint
npm run lint

# Build for production
npm run build
```

Frontend runs at http://localhost:3000

## Database Configuration

### Development (Automatic with Dev Services)
Quarkus Dev Services automatically provides PostgreSQL during development:
```bash
cd backend
./mvnw quarkus:dev
```
- Automatically starts PostgreSQL container (postgres:15-alpine)
- No manual database setup needed
- Requires Docker to be running
- Data is ephemeral (resets on restart)

### Testing (H2 In-Memory)
Tests use H2 in-memory database (no Docker required):
```bash
cd backend
./mvnw test
```
- Fast test execution
- No external dependencies
- Automatic cleanup between tests

### Production (Docker Compose)
Database credentials are configured in `docker-compose.yml`:
- **Database:** classroom_db
- **User:** teacher
- **Password:** securepassword
- **Port:** 5432

Connection string: `jdbc:postgresql://localhost:5432/classroom_db`

### Configuration Files
- `backend/src/main/resources/application.properties` - Main config (Dev Services enabled)
- `backend/src/test/resources/application.properties` - Test config (H2 override)

## API Endpoints

### Current Endpoints
- `GET /hello` - Test endpoint (returns "Hello from Quarkus REST")

### Planned Endpoints
- `GET /api/sessions` - List all focus sessions
- `POST /api/sessions` - Create new session
- `PATCH /api/sessions/{id}` - Update session
- `DELETE /api/sessions/{id}` - Delete session
- `POST /api/sessions/{id}/students` - Add student to session

See `.kiro/steering/api-conventions.md` for API design patterns.

## Development Workflow

This project uses **spec-driven development** for incremental feature building.

### Creating a Feature

1. **Write a spec** in `.kiro/specs/NNN-feature-name.md`:
```markdown
# Feature Name

## Goal
One sentence describing what we're building.

## Acceptance Criteria
- [ ] Criterion 1 (testable)
- [ ] Criterion 2 (testable)

## Technical Notes
- Implementation hints
- API endpoints
- Database changes
```

2. **Implement the feature** following project conventions:
   - API patterns: `.kiro/steering/api-conventions.md`
   - Database patterns: `.kiro/steering/database-conventions.md`
   - Testing standards: `.kiro/steering/testing-standards.md`

3. **Run tests** to verify:
```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npx tsc --noEmit
```

4. **Mark spec as done** by prefixing filename with `[DONE]`

### Example Specs
See `.kiro/specs/000-example-spec.md` for a template.

## Testing

### Backend Tests
```bash
cd backend
./mvnw test                    # Run all tests
./mvnw test -Dtest=MyTest      # Run specific test
```

Tests use:
- **JUnit 5** for test framework
- **RestAssured** for API testing
- **Quarkus Test** for integration testing
- **H2 in-memory database** for test isolation

### Frontend Tests
```bash
cd frontend
npx tsc --noEmit              # Type checking
npm run lint                  # Linting
npm run build                 # Build verification
```

### E2E Tests (Full Stack)

End-to-end tests use Playwright to test the complete application flow:

```bash
# Run E2E tests (automated script)
bash test-e2e.sh

# Or manually:
# 1. Start backend
cd backend && ./mvnw quarkus:dev

# 2. Start frontend (in another terminal)
cd frontend && npm run dev

# 3. Run Playwright tests (in another terminal)
cd frontend && npm run test:e2e

# Run with UI for debugging
npm run test:e2e:ui

# Run in headed mode (see browser)
npm run test:e2e:headed
```

**E2E Test Coverage:**
- Navigation between pages
- Classroom creation flow
- URL addition and validation
- Error handling
- Empty states

**Note:** E2E tests run sequentially (not in parallel) to avoid database conflicts. They use the same database as the backend dev server.

### Pre-commit Hooks

Set up Git hooks to run checks before every commit:

```bash
# One-time setup
bash setup-hooks.sh
```

This configures Git to automatically run:
- **Frontend:** ESLint + TypeScript type checking
- **Backend:** Maven tests

To skip hooks (not recommended):
```bash
git commit --no-verify
```

### CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

**On every push/PR:**
1. **Backend Job:** Builds and runs all unit tests with Maven
2. **Frontend Job:** Type checks, lints, and builds with npm
3. **E2E Job:** Runs full-stack Playwright tests (depends on backend + frontend passing)

**Pipeline configuration:** `.github/workflows/ci.yml`

The E2E job:
- Starts backend with Quarkus dev mode
- Builds and starts frontend production server
- Waits for services to be ready
- Runs Playwright tests
- Uploads test reports as artifacts (available for 7 days)

All jobs must pass for the pipeline to succeed.

### Integration Testing

The E2E tests provide automated integration testing of the full stack. For manual testing:

```bash
# Start all services
docker-compose up -d

# Test backend API
curl http://localhost:8080/api/classrooms

# Test frontend
open http://localhost:3000

# Stop services
docker-compose down
```

## Project Guidelines

All development guidelines are in `.kiro/steering/`:
- `project-identity.md` - Tech stack and environment
- `product-vision.md` - Product goals and roadmap
- `architecture.md` - System architecture
- `api-conventions.md` - REST API patterns
- `database-conventions.md` - Entity and database patterns
- `kotlin-maven-standards.md` - Backend coding standards
- `testing-standards.md` - Testing requirements
- `spec-workflow.md` - Development workflow

## Building for Production

### Backend
```bash
cd backend
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Frontend
```bash
cd frontend
npm run build
npm start
```

### Docker Images
```bash
# Build all services
docker-compose build

# Run in production mode
docker-compose up -d
```

## Environment Variables

### Backend
- `QUARKUS_DATASOURCE_JDBC_URL` - Database connection string
- `QUARKUS_DATASOURCE_USERNAME` - Database user
- `QUARKUS_DATASOURCE_PASSWORD` - Database password

### Frontend
- `NEXT_PUBLIC_API_URL` - Backend API URL (default: http://localhost:8080)

## Troubleshooting

### Backend won't start
- Check Java version: `java -version` (need 21+)
- Check if port 8080 is available
- Check database connection in `application.properties`

### Frontend won't start
- Check Node version: `node -v` (need 20+)
- Run `npm install` to install dependencies
- Check if port 3000 is available

### Database connection issues
- Ensure PostgreSQL is running (Docker Compose handles this)
- Check credentials in `docker-compose.yml` and `application.properties`
- For dev mode, Quarkus Dev Services handles database automatically

### Tests failing
- Ensure all dependencies are installed
- Check that database is accessible
- Run `./mvnw clean test` to clean and retest

## Contributing

1. Create a spec in `.kiro/specs/`
2. Implement following project conventions
3. Write tests for all new code
4. Ensure all tests pass
5. Update documentation as needed

## License

[Add your license here]

## Product Vision

Classroom Guard bridges the gap between static school internet filters and the dynamic needs of a 45-minute lesson. Teachers get a "Magic Button" to control iPad internet access in real-time, with students joining via QR code.

**Current Phase:** NextDNS Bridge - Using NextDNS API for internet control
**Future Phase:** Custom DNS engine for full control

See `.kiro/steering/product-vision.md` for detailed roadmap.

## Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [Next.js Documentation](https://nextjs.org/docs)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
