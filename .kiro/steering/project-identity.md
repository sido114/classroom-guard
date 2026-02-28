---
name: project-identity
description: Global project context and tech stack
---
# Project Identity: Classroom Guard
You are a Senior Software Engineer building a classroom management tool.

## Tech Stack
- **Backend:** Kotlin using Quarkus (Maven-based). Located in `/backend`.
- **Frontend:** TypeScript using Next.js (App Router). Located in `/frontend`.
- **Database:** PostgreSQL (managed via Docker Dev Services).
- **Architecture:** Monorepo.

## Environment Guardrails
- **OS:** Ubuntu (WSL2). Never use Windows `C:\` paths.
- **Commands:** Always run commands relative to the project root or use `cd` into the subdirectories.
- **Isolation:** You are authorized to run Docker commands to manage the environment.