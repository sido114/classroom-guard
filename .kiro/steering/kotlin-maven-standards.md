---
name: kotlin-maven-standards
description: Standards for Backend development
---
# Kotlin & Maven Standards
- **Language:** Kotlin 2.x. Use idiomatic Kotlin (data classes, scope functions).
- **Build Tool:** Maven (`mvnw`). Do NOT use Gradle.
- **Quarkus:** Use RESTEasy Reactive and Panache for ORM.
- **Testing:** Every new endpoint must have a corresponding `@QuarkusTest` in Kotlin.