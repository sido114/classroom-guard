package org.acme

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class HealthResourceTest {

    @Test
    fun `should return 200 and status UP`() {
        given()
        .`when`()
            .get("/api/health")
        .then()
            .statusCode(200)
            .body("status", `is`("UP"))
    }
}
