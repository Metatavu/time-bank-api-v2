package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.test.client.models.Person
import fi.metatavu.timebank.test.client.models.Timespan
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

/**
 * Tests for Person API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonsTest: AbstractTest() {

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
     * Tests /v1/persons?active=false -endpoint
     */
    @Test
    fun listActivePersons() {
        createTestBuilder().use { testBuilder ->
            val persons = testBuilder.manager.persons.getPersons(active = false)

            assertEquals(4, persons.size)
            testBuilder.notValid.persons.assertListFail(401)
        }
    }

    /**
     * Tests /v1/persons -endpoint
     */
     @Test
    fun listAllPersons() {
        createTestBuilder().use { testBuilder ->
            val persons = testBuilder.manager.persons.getPersons()

            assertEquals(4, persons.size)
            assertEquals("TesterA", persons[0].firstName)
            assertEquals(29, persons[0].unspentVacations)
            assertEquals(1, persons[0].spentVacations)
            assertEquals(10, persons[0].minimumBillableRate)
            assertEquals(25, persons[1].minimumBillableRate)
            testBuilder.notValid.persons.assertListFail(401)
        }
    }

    /**
     * Tests handling Keycloak user attributes via v/1/persons/{personId} -endpoint PUT method
     */
    @Test
    fun updatePersons() {
        createTestBuilder().use { testBuilder ->
            val person = testBuilder.manager.persons.getPersons(active = false).find { it.id == 2 }!!
            val secondPerson = testBuilder.manager.persons.getPersons(active = false).find { it.id == 3}!!
            val newPerson = Person(
                id = person.id,
                firstName = person.firstName,
                lastName = person.lastName,
                email = person.email,
                monday = person.monday,
                tuesday = person.tuesday,
                wednesday = person.wednesday,
                thursday = person.thursday,
                friday = person.friday,
                saturday = person.saturday,
                sunday = person.sunday,
                active = person.active,
                unspentVacations = person.unspentVacations,
                spentVacations = person.spentVacations,
                minimumBillableRate = 75,
                language = person.language,
                startDate = person.startDate
            )
            val updatedPerson = testBuilder.manager.persons.updatePerson(
                personId = newPerson.id,
                person = newPerson
            )

            assertEquals(25,person .minimumBillableRate)
            assertEquals(75, updatedPerson.minimumBillableRate)
            testBuilder.userA.persons.assertUpdateFail(
                person = newPerson,
                expectedStatus = 401
            )
            testBuilder.manager.persons.assertUpdateFail(
                person = secondPerson,
                expectedStatus = 500
            )

            // Quarkus Devservices are not restarted between test runs,
            // hence resetting edited Keycloak attribute is necessary for consequent test runs.
            testBuilder.manager.persons.updatePerson(
                personId = person.id,
                person = person
            )
        }
    }

    /**
     * Tests /v1/persons/2/total -endpoint
     * timespan defaults to ALL_TIME
     */
    @Test
    fun listPersonTotalTimeForTesterBAllTime() {
        createTestBuilder().use { testBuilder ->
            val personTotalTimes = testBuilder.manager.persons.getPersonTotal(
                personId = 2,
                timespan = null
            )

            assertEquals(1, personTotalTimes.size)
            assertEquals(213, personTotalTimes[0].internalTime)
            assertEquals(1500, personTotalTimes[0].billableProjectTime)
            assertEquals(1713, personTotalTimes[0].logged)
        }
    }

    /**
     * Tests /v1/persons/2/total?timespan=ALL_TIME&before=yyyy-mm-dd
     */
    @Test
    fun listPersonTotalTimeForTesterBAllTimeWithoutToday() {
        createTestBuilder().use { testBuilder ->
            val personTotalTimes = testBuilder.manager.persons.getPersonTotal(
                    personId = 2,
                    timespan = Timespan.ALL_TIME,
                    before = LocalDate.now().minusDays(1).toString()
            )

            assertEquals(1, personTotalTimes.size)
            assertEquals(213, personTotalTimes[0].internalTime)
            assertEquals(1200, personTotalTimes[0].billableProjectTime)
            assertEquals(1413, personTotalTimes[0].logged)
        }
    }

    /**
     * Tests /v1/persons/3/total?timespan=MONTH
     */
    @Test
    fun listPersonTotalTimeForTesterCMonth() {
        createTestBuilder().use { testBuilder ->
            val amountOfMonths = ChronoUnit.MONTHS.between(
                getThirtyDaysAgo().withDayOfMonth(1),
                LocalDate.now().plusMonths(1).withDayOfMonth(1)
            )
            val personTotalTimes = testBuilder.manager.persons.getPersonTotal(
                personId = 3,
                timespan = Timespan.MONTH
            )

            assertEquals(amountOfMonths.toInt(), personTotalTimes.size)
            assertTrue(personTotalTimes.find { it.nonBillableProjectTime == 122 } != null)
            assertTrue(personTotalTimes.find { it.internalTime == 750 } != null)
            assertTrue(personTotalTimes.find { it.billableProjectTime == 300 || it.billableProjectTime == 352 } != null)
            assertTrue(personTotalTimes.find { it.loggedProjectTime == 300 || it.loggedProjectTime == 352 } != null)
            assertTrue(personTotalTimes.all { it.balance < 0 } )
        }
    }

    /**
     * Tests /v1/persons/3/total?timespan=WEEK
     */
    @Test
    fun listPersonTotalTimeForTesterCWeek() {
        createTestBuilder().use { testBuilder ->
            val personTotalTimes = testBuilder.manager.persons.getPersonTotal(
                personId = 3,
                timespan = Timespan.WEEK
            )

            val expectedWeeks = ceil(daysBetweenMonth / 7.toDouble()).toInt()

            assertEquals(expectedWeeks, personTotalTimes.size)
            assertTrue(personTotalTimes.find { it.internalTime == 372 } != null)
            assertTrue(personTotalTimes.find { it.billableProjectTime == 122 } != null)
            assertTrue(personTotalTimes.find { it.logged == 494 } != null)
            assertTrue(personTotalTimes.find { it.nonBillableProjectTime == 122 } != null)
            assertTrue(personTotalTimes.find { it.billableProjectTime == 52 } != null)
            assertTrue(personTotalTimes.find { it.internalTime == 378 } != null)
            personTotalTimes.forEach {
                assertTrue(it.balance < 0)
            }
        }
    }

    /**
     * Tests listing total time entries for a non-existing person
     */
    @Test
    fun listNonExistingPersonTimeEntries() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.persons.assertTotalsFail(
                expectedStatus = 404,
                personId = 123
            )
        }
    }

    /**
     * Test /v1/persons?active=false without access token
     */
     @Test
    fun listPersonsWithNullToken() {
        createTestBuilder().use { testBuilder ->
            testBuilder.userWithNullToken.persons.assertListFailWithNullToken(expectedStatus = 401)
        }
    }

    /**
     * Tests /v1/persons -endpoint when Forecast API persons -endpoint responses with an error
     */
     @Test
    fun testPersonsWithForecastError() {
        setScenario(
            scenario = PERSONS_SCENARIO,
            state = ERROR_STATE
        )
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.persons.assertListFail(expectedStatus = 400)
        }
    }
}