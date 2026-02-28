# Quick Start Guide for Spec-Driven Development

## Create Your First Spec

1. **Create file** `.kiro/specs/001-feature-name.md`
2. **Use template:**
```markdown
# [Feature Name]

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

3. **Tell Kiro:** "Implement spec 001"
4. **Kiro will:** Implement, test, and report results

## Example Specs

### Session Entity
```markdown
# Session Entity

## Goal
Create a Session entity to represent a classroom focus session.

## Acceptance Criteria
- [ ] Session entity with id, name, createdAt, isActive fields
- [ ] Uses Panache pattern
- [ ] Table name is "sessions" (snake_case)

## Technical Notes
- File: backend/src/main/kotlin/org/acme/Session.kt
- Follow database-conventions.md
```

### Session List API
```markdown
# Session List API

## Goal
Add GET /api/sessions endpoint to list all sessions.

## Acceptance Criteria
- [ ] GET /api/sessions returns 200
- [ ] Response is JSON array
- [ ] Test exists and passes

## Technical Notes
- File: backend/src/main/kotlin/org/acme/SessionResource.kt
- Add test in SessionResourceTest.kt
```

## Manual Testing

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npx tsc --noEmit

# Full stack
docker-compose up --build
```

## Tips

- Keep specs small (15-30 min each)
- Make criteria testable
- One feature per spec
- Test after every spec
