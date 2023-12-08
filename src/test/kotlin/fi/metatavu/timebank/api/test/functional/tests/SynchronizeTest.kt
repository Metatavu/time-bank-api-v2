package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getSixtyDaysAgo
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.quarkus.test.vertx.RunOnVertxContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate

/**
 * Tests for Synchronization API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RunOnVertxContext
class SynchronizeTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    /**
     * Tests /v1/synchronize -endpoint
     * with mock Forecast data from past 30 and 60 days.
     */
    @Test
    @RunOnVertxContext
    fun testSynchronization() {
        createTestBuilder().use { testBuilder ->
            val amountOfPersons = testBuilder.manager.persons.getPersons().size

            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = getThirtyDaysAgo().toString()
            )
            val synchronizedFirst = testBuilder.manager.timeEntries.getTimeEntries()
            val expectedFirst = daysBetweenMonth * amountOfPersons
            synchronizedFirst.forEach { testBuilder.manager.timeEntries.clean(it) }

            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = getSixtyDaysAgo().toString()
            )
            val synchronizedSecond = testBuilder.manager.timeEntries.getTimeEntries()
            val expectedSecond = daysBetweenTwoMonths * amountOfPersons
            synchronizedSecond.forEach { testBuilder.manager.timeEntries.clean(it) }

            assertEquals(expectedFirst.toInt(), synchronizedFirst.size)
            assertEquals(expectedSecond.toInt(), synchronizedSecond.size)
        }
    }

    /**
     * Tests /v1/synchronize -endpoint
     * when Forecast API response contains updated entry
     */
    @Test
    @RunOnVertxContext
    fun testSynchronizationUpdate() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = getThirtyDaysAgo().toString()
            )
            val synchronizedFirst = testBuilder.manager.timeEntries.getTimeEntries()
            synchronizedFirst.forEach { testBuilder.manager.timeEntries.clean(it) }

            setScenario(
                scenario = TIMES_SCENARIO,
                state = UPDATE_STATE_TWO
            )

            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = LocalDate.now().minusDays(1).toString()
            )
            val synchronizedSecond = testBuilder.manager.timeEntries.getTimeEntries()
            synchronizedSecond.forEach { testBuilder.manager.timeEntries.clean(it) }

            assertFalse(synchronizedFirst.first() == synchronizedSecond.first())

        }
    }

    /**
     * Tests /v1/synchronizeDeletions -endpoint
     */
    @Test
    @RunOnVertxContext
    fun testDeletedSynchronization() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.synchronization.synchronizeEntries()

            var timeBankTimeEntries = testBuilder.manager.timeEntries.getTimeEntries()

            assertTrue(timeBankTimeEntries.find { it.forecastId == 20} != null)

            setScenario(
                scenario = TIMES_SCENARIO,
                state = DELETE_STATE
            )

            testBuilder.manager.synchronization.synchronizeEntries(syncDeleted = true)
            timeBankTimeEntries = testBuilder.manager.timeEntries.getTimeEntries()
            timeBankTimeEntries.forEach { testBuilder.manager.timeEntries.clean(it) }

            assertFalse(timeBankTimeEntries.find { it.forecastId == 20 } != null)
        }
    }
}