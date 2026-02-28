# Testing Standards

## Backend Testing (Kotlin + Quarkus)

### Test Database
Tests use H2 in-memory database (no Docker required):
- Configured in `backend/src/test/resources/application.properties`
- Automatic setup and teardown
- Fast and isolated

### Every REST Endpoint Needs
1. **Unit Test** (`@QuarkusTest`)
   - Test happy path
   - Test validation errors
   - Test edge cases

2. **Test Structure**
```kotlin
@QuarkusTest
class MyResourceTest {
    
    @Test
    fun `should return 200 when valid request`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"field": "value"}""")
        .`when`()
            .post("/api/endpoint")
        .then()
            .statusCode(200)
            .body("field", `is`("expected"))
    }
    
    @Test
    fun `should return 400 when invalid request`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"field": ""}""")
        .`when`()
            .post("/api/endpoint")
        .then()
            .statusCode(400)
    }
}
```

### Database Testing
- Use `@Transactional` for test isolation
- H2 provides automatic test database
- Clean state between tests

### Running Tests
```bash
cd backend
./mvnw test                    # Run all tests
./mvnw test -Dtest=MyTest      # Run specific test
./mvnw verify                  # Run integration tests too
```

## Frontend Testing (TypeScript + Next.js)

### Type Safety First
- Run `npx tsc --noEmit` to catch type errors
- No `any` types without justification
- Strict mode enabled

### Running Checks
```bash
cd frontend
npm run lint                   # ESLint
npx tsc --noEmit              # Type check
npm run build                 # Production build test
```

## Integration Testing

### API Contract Testing
- Backend defines the contract (OpenAPI/Swagger in future)
- Frontend consumes it
- Both sides test against the contract

### Docker Compose Testing
```bash
# Full stack smoke test
docker-compose up -d
curl http://localhost:8080/hello  # Should return 200
curl http://localhost:3000        # Should return 200
docker-compose down
```

## Test Coverage Goals
- Backend: Aim for 80%+ coverage on business logic
- Frontend: Focus on critical user flows
- Don't test framework code, test YOUR code

## When Tests Fail
1. Read the error message carefully
2. Check if it's a real bug or a test issue
3. Fix the code, not the test (usually)
4. Re-run to confirm
5. Commit only when green

## Continuous Testing
- Run tests before every commit
- Use `./mvnw quarkus:dev` for backend live reload
- Use `npm run dev` for frontend hot reload
- Fix broken tests immediately, don't accumulate debt
