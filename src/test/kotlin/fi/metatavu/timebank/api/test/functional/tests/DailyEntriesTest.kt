package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Tests for DailyEntries API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DailyEntriesTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    /**
     * Runs synchronization before tests to make sure database is populated
     */
    @BeforeAll
    fun synchronizeBeforeTests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = getThirtyDaysAgo().toString()
            )
        }
    }

    /**
     * Cleans persisted timeEntries after all tests
     */
    @AfterAll
    fun cleanSynchronized() {
        createTestBuilder().use { testBuilder ->
            val entries = testBuilder.manager.timeEntries.getTimeEntries()
            entries.forEach { timeEntry ->
                testBuilder.manager.timeEntries.clean(timeEntry)
            }
        }
    }

    /**
     * Tests /v1/dailyEntries -endpoint
     */
    @Test
    fun listDailyEntries() {
        createTestBuilder().use { testBuilder ->
            val personA = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 1,
                before = null,
                after = null,
                vacation = null
            )
            val personB = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 2,
                before = null,
                after = null,
                vacation = true
            )
            val personC = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 3,
                before = getThirtyDaysAgo().toString(),
                after = null,
                vacation = false
            )
            val personD = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 5,
                before = null,
                after = LocalDate.now().toString(),
                vacation = false
            )

            assertEquals(daysBetweenMonth.toInt(), personA.size)
            assertEquals(1, personB.size)
            assertEquals(1, personC.size)
            assertEquals(1, personD.size)

            testBuilder.manager.dailyEntries.assertListFail(404)
        }
    }

    /**
     * Tests /v1/dailyEntries -endpoint when
     * Forecast API cannot be reached via different requests
     */
    @Test
    fun dailyEntriesErrorTest() {
        createTestBuilder().use { testBuilder ->

            setScenario(
                scenario = PERSONS_SCENARIO,
                state = ERROR_STATE
            )

            testBuilder.manager.dailyEntries.assertListFail(500)

            setScenario(
                scenario = HOLIDAYS_SCENARIO,
                state = ERROR_STATE
            )

            testBuilder.manager.dailyEntries.assertListFail(500)

        }
    }

    /**
     * Tests WorktimeCalendars functionality
     * when persons expected worktime has changed.
     */
    @Test
    fun testWorktimeCalendars() {
        createTestBuilder().use { testBuilder ->
            val firstEntries = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 5,
                before = LocalDate.now().minusDays(1).toString()
            )

            setScenario(
                scenario = PERSONS_SCENARIO,
                state = UPDATE_STATE_ONE
            )
            setScenario(
                scenario = TIMES_SCENARIO,
                state = UPDATE_STATE_ONE
            )

            testBuilder.manager.synchronization.synchronizeEntries(
                before = null,
                after = LocalDate.now().toString()
            )

            val secondEntries = testBuilder.manager.dailyEntries.getDailyEntries(
                personId = 5,
                after = LocalDate.now().toString()
            )

            firstEntries.forEach{ assertTrue(it.expected == 435 || it.expected == 0) }
            assertTrue(secondEntries.find{ it.expected == 217} != null)
        }
    }
}