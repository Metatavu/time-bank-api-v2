package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getSixtyDaysAgo
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

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

            assertTrue(expectedFirst.toInt() <= synchronizedFirst.size)
            assertTrue(expectedSecond.toInt() <= synchronizedSecond.size)
        }
    }

    /**
     * Tests /v1/synchronize -endpoint
     * when Forecast API response contains updated entry
     */
    @Test
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
}