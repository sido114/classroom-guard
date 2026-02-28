# Kiro's Responsibilities in Spec-Driven Development

## When User Says "Implement spec XXX"

### 1. Read the Spec
- Locate `.kiro/specs/XXX-feature-name.md`
- Parse the Goal, Acceptance Criteria, and Technical Notes
- Understand what needs to be built

### 2. Implement the Feature
- Follow the technical notes
- Apply all steering file conventions:
  - API conventions for endpoints
  - Database conventions for entities
  - Kotlin standards for code style
  - Testing standards for tests
- Write minimal, focused code
- Create tests alongside implementation

### 3. Run Tests Automatically
After implementation, ALWAYS run:

#### Backend Changes
```bash
cd backend
./mvnw clean test
```
- Report pass/fail status
- Show any error messages
- Suggest fixes if tests fail

#### Frontend Changes
```bash
cd frontend
npx tsc --noEmit
```
- Report type errors
- Show compilation issues
- Suggest fixes if needed

#### Both Changed
Run both test suites and report combined results.

**Note:** Tests use H2 in-memory database (no Docker/PostgreSQL needed for testing)

### 4. Verify Acceptance Criteria
- Check each criterion against implementation
- Mark completed criteria with [x]
- Report which criteria are met
- Identify any gaps

### 5. Report Results
Provide a concise summary:
```
✓ Session entity created
✓ Tests pass (3/3)
✓ All acceptance criteria met

Ready for next spec!
```

## During Implementation

### Follow Conventions
- **API**: Use patterns from `api-conventions.md`
- **Database**: Use patterns from `database-conventions.md`
- **Testing**: Use patterns from `testing-standards.md`
- **Kotlin**: Use patterns from `kotlin-maven-standards.md`

### Write Tests First (or Alongside)
- Every endpoint needs a test
- Every entity should be testable
- Tests verify acceptance criteria

### Keep It Minimal
- Only implement what the spec requires
- Don't add extra features
- Don't over-engineer
- Focus on acceptance criteria

## After Implementation

### Test Execution
1. Run backend tests if .kt files changed
2. Run frontend type check if .ts/.tsx files changed
3. Report results immediately
4. Don't proceed if tests fail

### Fix Failures
If tests fail:
1. Read the error message
2. Identify the issue
3. Fix the code
4. Re-run tests
5. Confirm green before reporting complete

### Update Spec Status
- Mark acceptance criteria as complete
- Update spec filename if needed: `[DONE] 001-feature.md`
- Suggest next spec to implement

## Testing Patterns

### Backend Test Template
```kotlin
@QuarkusTest
class FeatureResourceTest {
    
    @Test
    fun `should return 200 when valid request`() {
        given()
            .contentType(ContentType.JSON)
        .`when`()
            .get("/api/endpoint")
        .then()
            .statusCode(200)
    }
    
    @Test
    fun `should return 404 when not found`() {
        given()
        .`when`()
            .get("/api/endpoint/999")
        .then()
            .statusCode(404)
    }
}
```

### Frontend Type Check
```bash
npx tsc --noEmit
```
- Must pass with zero errors
- Fix any type issues before marking complete

## Common Scenarios

### Scenario: Backend Endpoint Spec
1. Create entity (if needed)
2. Create resource class
3. Add endpoint method
4. Create test class
5. Add test methods
6. Run `./mvnw test`
7. Report results

### Scenario: Frontend Component Spec
1. Create component file
2. Add TypeScript types
3. Implement component
4. Run `npx tsc --noEmit`
5. Run `npm run build`
6. Report results

### Scenario: Database Entity Spec
1. Create entity class
2. Add Panache companion
3. Configure table/columns
4. Update application.properties if needed
5. Run `./mvnw test`
6. Verify entity works

## Error Handling

### When Tests Fail
1. **Don't panic** - Read the error
2. **Identify root cause** - Is it code or test?
3. **Fix the issue** - Usually fix code, not test
4. **Re-run** - Confirm fix works
5. **Report** - Explain what was wrong and how it was fixed

### When Spec is Unclear
1. **Ask for clarification** - Don't guess
2. **Suggest improvements** - Help make spec clearer
3. **Propose acceptance criteria** - If missing

### When Dependencies Missing
1. **Identify the blocker** - What's needed?
2. **Suggest prerequisite spec** - What should be built first?
3. **Mark as [BLOCKED]** - Update spec status

## Quality Standards

### Code Quality
- Idiomatic Kotlin
- Type-safe TypeScript
- No compiler warnings
- Clean, readable code

### Test Quality
- Tests verify acceptance criteria
- Tests are deterministic
- Tests are isolated
- Tests are fast

### Documentation Quality
- Code comments where needed
- API endpoints documented
- Complex logic explained
- Steering files updated

## Communication Style

### Be Concise
- Short, clear status updates
- No unnecessary verbosity
- Focus on results

### Be Helpful
- Suggest next steps
- Offer improvements
- Explain failures clearly

### Be Proactive
- Run tests without being asked
- Catch issues early
- Suggest better approaches

## Success Criteria

A spec is complete when:
- ✓ All acceptance criteria met
- ✓ All tests pass
- ✓ No type errors
- ✓ Code follows conventions
- ✓ Documentation updated
- ✓ Ready for next spec

## Remember

- **Small iterations** - One spec at a time
- **Test everything** - No untested code
- **Follow conventions** - Consistency matters
- **Report clearly** - User needs to know status
- **Fix immediately** - Don't accumulate debt
