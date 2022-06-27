package fi.metatavu.timebank.api.test.functional.tests

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

/**
 * Tests for System API
 */
@QuarkusTest
class SystemTest {

    /**
     * Tests /v1/system/ping -endpoint
     */
    @Test
    fun testPingEndpoint() {
        given()
            .contentType("application/json")
            .`when`().get("http://localhost:8081/v1/system/ping")
            .then()
            .statusCode(200)
            .body(`is`("Pong"))
    }
}