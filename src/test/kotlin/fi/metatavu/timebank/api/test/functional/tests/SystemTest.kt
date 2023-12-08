package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.vertx.RunOnVertxContext
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

/**
 * Tests for System API
 */
@QuarkusTest
@TestProfile(LocalTestProfile::class)
class SystemTest {

    /**
     * Tests /v1/system/ping -endpoint
     */
    @Test
    @RunOnVertxContext
    fun testPingEndpoint() {
        given()
            .contentType("application/json")
            .`when`().get("http://localhost:8081/v1/system/ping")
            .then()
            .statusCode(200)
            .body(`is`("Pong"))
    }
}