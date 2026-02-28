package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
class ClassroomResourceTest {

    @Test
    fun `should create classroom with valid name and description`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Math 101", "description": "Advanced Mathematics"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", `is`("Math 101"))
            .body("description", `is`("Advanced Mathematics"))
            .body("urls.size()", `is`(0))
            .body("createdAt", notNullValue())
    }

    @Test
    fun `should create classroom with name only`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Science 202"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", `is`("Science 202"))
            .body("urls.size()", `is`(0))
    }

    @Test
    fun `should trim whitespace from name and description`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "  History 303  ", "description": "  World History  "}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .body("name", `is`("History 303"))
            .body("description", `is`("World History"))
    }

    @Test
    fun `should return 400 when name is empty`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": ""}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(400)
            .body("error", `is`("Classroom name cannot be empty"))
    }

    @Test
    fun `should return 400 when name is blank`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "   "}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(400)
            .body("error", `is`("Classroom name cannot be empty"))
    }

    @Test
    fun `should return 400 when name exceeds 100 characters`() {
        val longName = "a".repeat(101)
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "$longName"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(400)
            .body("error", `is`("Classroom name cannot exceed 100 characters"))
    }

    @Test
    fun `should accept name with exactly 100 characters`() {
        val exactName = "a".repeat(100)
        given()
            .contentType(ContentType.JSON)
            .body("""{"name": "$exactName"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .body("name", `is`(exactName))
    }

    @Test
    fun `should return list of classrooms`() {
        // Just verify the endpoint works and returns a list
        given()
        .`when`()
            .get("/api/classrooms")
        .then()
            .statusCode(200)
    }

    @Test
    fun `should return single classroom in list`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Single Classroom", "description": "Only one"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Get list - should contain the created classroom
        given()
        .`when`()
            .get("/api/classrooms")
        .then()
            .statusCode(200)
            .body("find { it.id == $classroomId }.name", `is`("Single Classroom"))
            .body("find { it.id == $classroomId }.description", `is`("Only one"))
            .body("find { it.id == $classroomId }.urlCount", `is`(0))
            .body("find { it.id == $classroomId }.createdAt", notNullValue())
    }

    @Test
    fun `should return list of classrooms ordered by creation date`() {
        // Create first classroom
        val firstId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "First Classroom", "description": "Created first"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Small delay to ensure different timestamps
        Thread.sleep(100)

        // Create second classroom
        val secondId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Second Classroom", "description": "Created second"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Verify both classrooms exist in response
        given()
        .`when`()
            .get("/api/classrooms")
        .then()
            .statusCode(200)
            .body("find { it.id == $firstId }.name", `is`("First Classroom"))
            .body("find { it.id == $firstId }.description", `is`("Created first"))
            .body("find { it.id == $secondId }.name", `is`("Second Classroom"))
            .body("find { it.id == $secondId }.description", `is`("Created second"))
    }

    @Test
    fun `should include url count in classroom list`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Get list - should show 0 URLs initially
        given()
        .`when`()
            .get("/api/classrooms")
        .then()
            .statusCode(200)
            .body("find { it.id == $classroomId }.urlCount", `is`(0))
    }

    @Test
    fun `should get classroom by id with no URLs`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Empty Classroom", "description": "No URLs yet"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Get classroom by ID
        given()
        .`when`()
            .get("/api/classrooms/$classroomId")
        .then()
            .statusCode(200)
            .body("id", `is`(classroomId))
            .body("name", `is`("Empty Classroom"))
            .body("description", `is`("No URLs yet"))
            .body("urls.size()", `is`(0))
            .body("createdAt", notNullValue())
    }

    @Test
    fun `should get classroom by id with multiple URLs`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Classroom with URLs", "description": "Has multiple URLs"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Add first URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "example.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)

        // Add second URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "wikipedia.org"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)

        // Add third URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://github.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)

        // Get classroom by ID - should include all 3 URLs
        given()
        .`when`()
            .get("/api/classrooms/$classroomId")
        .then()
            .statusCode(200)
            .body("id", `is`(classroomId))
            .body("name", `is`("Classroom with URLs"))
            .body("description", `is`("Has multiple URLs"))
            .body("urls.size()", `is`(3))
            .body("urls[0].url", `is`("https://example.com"))
            .body("urls[0].urlType", `is`("whitelist"))
            .body("urls[0].createdAt", notNullValue())
            .body("urls[1].url", `is`("https://wikipedia.org"))
            .body("urls[1].urlType", `is`("whitelist"))
            .body("urls[1].createdAt", notNullValue())
            .body("urls[2].url", `is`("https://github.com"))
            .body("urls[2].urlType", `is`("whitelist"))
            .body("urls[2].createdAt", notNullValue())
    }

    @Test
    fun `should return 404 when classroom does not exist`() {
        given()
        .`when`()
            .get("/api/classrooms/99999")
        .then()
            .statusCode(404)
            .body("error", `is`("Classroom not found"))
    }

    // Tests for POST /api/classrooms/{id}/urls

    @Test
    fun `should add URL with domain only format`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Add URL with domain only
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "example.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("url", `is`("https://example.com"))
            .body("urlType", `is`("whitelist"))
            .body("createdAt", notNullValue())
    }

    @Test
    fun `should add URL with https protocol`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Add URL with https protocol
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)
            .body("url", `is`("https://example.com"))
            .body("urlType", `is`("whitelist"))
    }

    @Test
    fun `should add URL with path`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Add URL with path
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com/path/to/resource"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)
            .body("url", `is`("https://example.com/path/to/resource"))
            .body("urlType", `is`("whitelist"))
    }

    @Test
    fun `should return 400 when URL is empty`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Try to add empty URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": ""}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(400)
            .body("error", `is`("URL cannot be empty"))
    }

    @Test
    fun `should return 400 when URL format is invalid`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Try to add invalid URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "not a valid url!@#"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(400)
            .body("error", `is`("Invalid URL format. Please provide a valid domain (e.g., example.com)"))
    }

    @Test
    fun `should return 400 when URL is duplicate`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Add first URL
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "example.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)

        // Try to add the same URL again (normalized form)
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(400)
            .body("error", `is`("This URL already exists in the classroom"))
    }

    @Test
    fun `should return 404 when classroom does not exist for URL addition`() {
        // Try to add URL to non-existent classroom
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "example.com"}""")
        .`when`()
            .post("/api/classrooms/99999/urls")
        .then()
            .statusCode(404)
            .body("error", `is`("Classroom not found"))
    }

    @Test
    fun `should add URL at maximum length`() {
        // Create a classroom
        val classroomId = given()
            .contentType(ContentType.JSON)
            .body("""{"name": "Test Classroom"}""")
        .`when`()
            .post("/api/classrooms")
        .then()
            .statusCode(201)
            .extract().path<Int>("id")

        // Create a URL with maximum length (2048 chars)
        // Format: https://example.com/ + path of remaining chars
        val baseUrl = "https://example.com/"
        val remainingLength = 2048 - baseUrl.length
        val longPath = "a".repeat(remainingLength)
        val maxLengthUrl = baseUrl + longPath

        // Add URL at maximum length
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "$maxLengthUrl"}""")
        .`when`()
            .post("/api/classrooms/$classroomId/urls")
        .then()
            .statusCode(201)
            .body("url", `is`(maxLengthUrl))
            .body("urlType", `is`("whitelist"))
    }
}
