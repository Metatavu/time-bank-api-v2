package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersTest: AbstractTest() {

    @Test
    fun listUsers(){
        createTestBuilder().use { testBuilder ->
            val users = testBuilder.manager.users.getUsers()
            val testerAUser = users.find { it.firstName == "Manager" }

            assertEquals(6, users.size)
            assertEquals(testerAUser!!.email, "manager@timebank.com")
        }
    }

    @Test
    fun findUser() {
        createTestBuilder().use { testBuilder ->
            val user = testBuilder.manager.users.findUser(UUID.fromString("eb4123a3-b722-4798-9af5-8957f823657a"))

            assertEquals(user.email, "testerb@example.com")
        }
    }

    @Test
    fun listUsersWithNullToken() {
        createTestBuilder().use { testBuilder ->
            testBuilder.userWithNullToken.users.assertListFailWithStatus(expectedStatus = 401)
        }
    }
}