package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.ForecastDeleteWebhookEvent
import fi.metatavu.timebank.test.client.models.ForecastDeleteWebhookObject
import fi.metatavu.timebank.test.client.models.ForecastDeleteWebhookPerson
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
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

    /**
     * Tests /v1/ForecastTimeEntriesDelete/webhook endpoint
     */
    @Test
    fun testForecastTimeEntriesDeleteWebhook() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.synchronization.synchronizeEntries(
                    before = null,
                    after = getThirtyDaysAgo().toString()
            )

            testBuilder.manager.timeEntries.forecastTimeEntriesDeleteWebhook(
                forecastDeleteWebhookEvent = ForecastDeleteWebhookEvent(
                    timestamp = "2023-02-21T15:42:00",
                    event = "Time_entry_deleted",
                    `object` = ForecastDeleteWebhookObject(id = 5),
                    person = ForecastDeleteWebhookPerson(id = 1)
                ),
                forecastDeleteWebhookKey = forecastKey
            )
            val timeEntries = testBuilder.manager.timeEntries.getTimeEntries()

            assertFalse(timeEntries.find { it.forecastId == 5 } != null)
            assertTrue(timeEntries.find { it.forecastId == 2 } != null)
            timeEntries.forEach { timeEntry -> testBuilder.manager.timeEntries.clean(timeEntry) }
        }
    }
}