# Testing Standards

## CRITICAL TESTING RULES

### Rule #1: Tests MUST Be Green
- **ALWAYS run `./mvnw test` (cwd: backend) after making changes**
- **NEVER skip or ignore failing tests**
- **NEVER try alternative commands when tests fail - FIX THE TESTS**
- **NEVER try `./mvnw clean test`, `./mvnw verify`, `bash mvnw`, or other variations**
- **ONLY use: `./mvnw test` (cwd: backend)**
- If tests fail, read the error message carefully and fix the actual problem
- Do not move forward until ALL tests pass
- If bash commands fail with exit code -1, use getDiagnostics tool instead

### Rule #2: Fix Tests Properly - NO RETRIES WITH DIFFERENT COMMANDS
- When tests fail, FIX THE CODE, don't try different commands
- Read the actual error message from Maven output or getDiagnostics
- Understand the root cause before making changes
- Test your fix by running `./mvnw test` (cwd: backend) again
- **FORBIDDEN**: Trying multiple test commands when one fails
- **CORRECT**: Fix the code, then run the same command again
- Common issues:
  - Type mismatches (Integer vs Long)
  - Database state between tests
  - Missing imports or wrong assertion libraries
  - Kotlin version incompatibilities (use CharArray().concatToString() not .repeat())
  - Stale diagnostics (code is fixed but diagnostics cached)

### Rule #3: Test Isolation and Shared Database
- Tests share a database in Quarkus - the database is NOT reset between tests
- **NEVER assume the database is empty** in your tests
- **NEVER check for exact counts** like `body("size()", is(0))` or `body("size()", is(1))`
- **ALWAYS verify specific data exists** using `find { it.id == $id }` or similar
- Either clean up in tests or make assertions flexible
- Use `@BeforeEach` if you need clean state
- Example of WRONG test: `body("size()", is(1))` - assumes no other data
- Example of CORRECT test: `body("find { it.id == $id }.name", is("Expected"))` - checks specific item

### Rule #4: NEVER Use `cd` in Commands
- **FORBIDDEN**: `cd backend && ./mvnw test`
- **CORRECT**: `./mvnw test` with `cwd: backend` parameter
- The `cd` command is NOT supported in bash execution
- ALWAYS use the `cwd` parameter to specify working directory
- This applies to ALL commands, not just Maven

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
# CORRECT - Use cwd parameter, NOT cd command
./mvnw test                    # Run all tests (cwd: backend)
./mvnw test -Dtest=MyTest      # Run specific test (cwd: backend)

# WRONG - Don't use cd in commands
cd backend && ./mvnw test      # This will FAIL - cd is not supported!

# WRONG - Don't try alternative commands when tests fail
./mvnw clean test              # NO - just fix the code
./mvnw verify                  # NO - just fix the code
bash mvnw test                 # NO - just fix the code
```

### When Bash Commands Fail (exit code -1)
- If `./mvnw test` returns exit code -1, use `getDiagnostics` tool instead
- getDiagnostics shows compilation errors without running Maven
- Fix the errors shown in diagnostics
- Diagnostics may be cached/stale - if code looks correct, proceed

### Critical Command Rules
- **NEVER use `cd` in bash commands** - it's not supported and will always fail
- **ALWAYS use the `cwd` parameter** to specify working directory
- **NEVER try multiple test commands** - if tests fail, FIX THE CODE
- Example: `command: "./mvnw test", cwd: "backend"`
- If a command fails, check if you're in the right directory first

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
