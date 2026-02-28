# Spec-Driven Development Workflow

## Philosophy: Small, Testable Iterations
- Each spec should be completable in 15-30 minutes
- Every spec must have clear acceptance criteria
- Test after every task completion
- Iterate fast, fail fast, learn fast

## Spec Structure (Minimal)
```markdown
# [Feature Name]

## Goal
One sentence describing what we're building.

## Acceptance Criteria
- [ ] Criterion 1 (testable)
- [ ] Criterion 2 (testable)
- [ ] Criterion 3 (testable)

## Technical Notes
- Any constraints or implementation hints
- API endpoints to create
- Database changes needed
```

## Testing Workflow

### After Each Task
1. **Backend Tests:** Run `./mvnw test` in `/backend`
2. **Frontend Build:** Run `npm run build` in `/frontend`
3. **Integration Check:** Verify API contracts match

### After Major Milestones
1. **Full Stack Test:** Start docker-compose and verify end-to-end
2. **Manual QA:** Test the actual user flow
3. **Update Documentation:** Keep steering files current

## Spec Granularity Examples

### ✅ GOOD (Small Spec)
- "Add a GET /api/sessions endpoint that returns empty array"
- "Create Session entity with id, name, createdAt fields"
- "Add a button to the dashboard that logs 'clicked' to console"

### ❌ TOO BIG (Split It)
- "Build the entire session management system"
- "Implement teacher dashboard with all features"
- "Complete NextDNS integration"

## Development Commands

### Backend (from `/backend`)
```bash
# Run tests
./mvnw test

# Run with live reload (dev mode)
./mvnw quarkus:dev

# Build
./mvnw clean package
```

### Frontend (from `/frontend`)
```bash
# Run tests (when we add them)
npm test

# Type check
npx tsc --noEmit

# Lint
npm run lint

# Build
npm run build
```

### Full Stack (from project root)
```bash
# Start everything
docker-compose up --build

# Stop everything
docker-compose down

# Clean restart
docker-compose down -v && docker-compose up --build
```

## Kiro's Testing Responsibilities
After completing each spec task, Kiro should:
1. Run backend tests automatically
2. Check for TypeScript errors in frontend
3. Report any failures immediately
4. Suggest fixes if tests fail
5. Confirm all acceptance criteria are met

## File Organization
- Specs live in `.kiro/specs/` directory
- Name format: `001-feature-name.md`, `002-next-feature.md`
- Keep completed specs for reference
- Mark status in filename: `[DONE]`, `[IN-PROGRESS]`, `[BLOCKED]`
