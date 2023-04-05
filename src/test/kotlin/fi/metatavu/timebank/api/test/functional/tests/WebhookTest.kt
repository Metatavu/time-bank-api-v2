package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.ForecastWebhookEvent
import fi.metatavu.timebank.test.client.models.ForecastWebhookObject
import fi.metatavu.timebank.test.client.models.ForecastWebhookPerson
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
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
class WebhookTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    /**
     * Tests /v1/ForecastWebhook -endpoint time_registration_deleted -event
     */
    @Test
    fun testForecastTimeEntriesDeleteWebhook() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.synchronization.synchronizeEntries(
                    before = null,
                    after = getThirtyDaysAgo().toString()
            )
            val firstTimeEntries = testBuilder.manager.timeEntries.getTimeEntries()

            testBuilder.manager.forecastWebhooks.forecastWebhook(
                forecastWebhookEvent = ForecastWebhookEvent(
                    timestamp = "2023-02-21T15:42:00",
                    event = "time_registration_deleted",
                    `object` = ForecastWebhookObject(id = 5),
                    person = ForecastWebhookPerson(id = 1)
                ),
                forecastWebhookKey = forecastKey
            )
            val secondTimeEntries = testBuilder.manager.timeEntries.getTimeEntries()

            assertTrue(firstTimeEntries.find { it.forecastId == 5 } != null)
            assertFalse(secondTimeEntries.find { it.forecastId == 5 } != null)
            assertTrue(firstTimeEntries.find { it.forecastId == 2 } != null)
            assertTrue(secondTimeEntries.find { it.forecastId == 2 } != null)
            firstTimeEntries.forEach { timeEntry -> testBuilder.manager.timeEntries.clean(timeEntry) }
            secondTimeEntries.forEach { timeEntry -> testBuilder.manager.timeEntries.clean(timeEntry) }
        }
    }
}