package fi.metatavu.timebank.api.test.functional.tests

import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach(){
        resetScenarios()
    }

    @Test
    fun listUsers(){
        createTestBuilder().use { testBuilder ->
            val users = testBuilder.manager.users.getUsers()
            println("Users received: ${users.contentToString()}")
            val testerAUser = users.find { it.firstName == "Manager" }

            assertEquals(6, users.size)
            assertEquals(testerAUser!!.email, "manager@timebank.com")
        }
    }

    @Test
    fun listUsersWithNullToken() {
        createTestBuilder().use { testBuilder ->
            testBuilder.userWithNullToken.users.assertListFailWithNullToken(expectedStatus = 401)
        }
    }
}