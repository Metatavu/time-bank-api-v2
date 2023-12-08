package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.vertx.RunOnVertxContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for TimeEntries API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeEntriesTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    /**
     * Tests /v1/timeEntries -endpoint
     */
    @Test
    @RunOnVertxContext
    fun testTimeEntries() {
        createTestBuilder().use { testBuilder ->
            val amountOfPersons = testBuilder.manager.persons.getPersons().size
            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = getThirtyDaysAgo().toString()
            )
            val timeEntries = testBuilder.manager.timeEntries.getTimeEntries()
            val vacations = testBuilder.manager.timeEntries.getTimeEntries(vacation = true)
            val expected = amountOfPersons * daysBetweenMonth

            assertEquals(expected.toInt(), timeEntries.size)
            assertEquals(2, vacations.size)
            testBuilder.userA.timeEntries.assertDeleteFail(401, timeEntries[0].id)
            timeEntries.forEach { timeEntry ->
                testBuilder.manager.timeEntries.clean(timeEntry)
            }
        }
    }
}