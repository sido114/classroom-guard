# Project Files Overview

## Essential Files (Keep These)

### Root Configuration
- `README.md` - Complete project setup and usage guide
- `docker-compose.yml` - Full stack orchestration
- `.gitignore` - Git ignore rules

### Backend (Kotlin + Quarkus)
- `backend/pom.xml` - Maven dependencies
- `backend/src/main/resources/application.properties` - Database, CORS, logging config
- `backend/src/main/resources/import.sql` - Seed data
- `backend/src/main/kotlin/org/acme/` - Application code
- `backend/src/test/kotlin/org/acme/` - Tests
- `backend/mvnw` - Maven wrapper (no Maven install needed)

### Frontend (Next.js + TypeScript)
- `frontend/package.json` - npm dependencies
- `frontend/tsconfig.json` - TypeScript config
- `frontend/next.config.ts` - Next.js config
- `frontend/src/app/` - App Router pages

### Spec-Driven Development

#### Steering Files (.kiro/steering/)
**Core Guidelines:**
1. `project-identity.md` - Tech stack, OS, environment rules
2. `product-vision.md` - Product goals and roadmap
3. `architecture.md` - System architecture and phases
4. `api-conventions.md` - REST API patterns
5. `database-conventions.md` - Entity and database patterns
6. `kotlin-maven-standards.md` - Backend coding standards
7. `testing-standards.md` - Testing requirements

**Workflow Guides:**
8. `spec-workflow.md` - Spec-driven development process
9. `quick-start-guide.md` - Getting started with specs
10. `kiro-responsibilities.md` - What Kiro does during implementation

#### Specs (.kiro/specs/)
- `README.md` - Spec templates and usage
- `000-example-spec.md` - Example spec to learn from
- `001-*.md`, `002-*.md`, etc. - Your feature specs (create as needed)

#### Hooks (.kiro/hooks/)
- `test-after-backend-edit.json` - Auto-test when .kt files change
- `check-frontend-types.json` - Auto-check when .ts/.tsx files change
- `test-after-spec-task.json` - Full test suite after spec completion

## File Purpose Summary

### Steering Files (10 files)
These guide development and ensure consistency:
- **Identity & Vision** (3 files): What we're building and why
- **Technical Standards** (4 files): How to write code
- **Workflow** (3 files): How to work with specs

All are essential for maintaining code quality and consistency.

### Hooks (3 files)
Automate testing during development:
- Backend changes → Run tests
- Frontend changes → Type check
- Spec completion → Full verification

Optional but highly recommended for catching issues early.

### Specs (2 files + your specs)
- `README.md` - Template and instructions
- `000-example-spec.md` - Learning example
- Your specs - Created as you build features

## What's NOT Included (Intentionally)

- No test framework setup needed (Quarkus includes JUnit, RestAssured)
- No database setup scripts (Quarkus Dev Services handles it)
- No complex build scripts (Maven and npm handle everything)
- No CI/CD config yet (add when needed)
- No authentication yet (Phase 2)

## Minimal Setup to Start

If you want the absolute minimum:

1. **Must Have:**
   - `README.md` - Setup instructions
   - `docker-compose.yml` - Run everything
   - Backend code + `pom.xml`
   - Frontend code + `package.json`
   - `application.properties` - Backend config

2. **Highly Recommended:**
   - `.kiro/steering/` - All 10 files (consistency)
   - `.kiro/specs/README.md` - Spec template
   - `.kiro/hooks/` - All 3 files (automated testing)

3. **Optional:**
   - `.kiro/specs/000-example-spec.md` - Delete after learning
   - This file - Delete after reading

## Next Steps

1. Read `README.md` for setup instructions
2. Review `.kiro/steering/quick-start-guide.md` for workflow
3. Create your first spec in `.kiro/specs/001-your-feature.md`
4. Start building!
