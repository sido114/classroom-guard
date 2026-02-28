package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
class SavedUrlResourceTest {

    @Test
    fun `should create a new URL`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "https://example.com"}""")
            .`when`().post("/api/urls")
            .then()
            .statusCode(201)
            .body("url", `is`("https://example.com"))
            .body("id", notNullValue())
            .body("createdAt", notNullValue())
    }

    @Test
    fun `should return 400 when URL is empty`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": ""}""")
            .`when`().post("/api/urls")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return 400 when URL is blank`() {
        given()
            .contentType(ContentType.JSON)
            .body("""{"url": "   "}""")
            .`when`().post("/api/urls")
            .then()
            .statusCode(400)
    }
}
