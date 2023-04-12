package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.RequestStatus
import fi.metatavu.timebank.test.client.models.VacationRequest
import fi.metatavu.timebank.test.client.models.VacationType
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

/**
 * Tests for Vacations API
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VacationRequestsTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    /**
     * Tests /v1/vacationRequest -endpoint
     */
    @Test
    fun testVacationRequest() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacations.createVacationRequests(
                vacationRequest = VacationRequest(
                    id = UUID.fromString("5c2b0646-8e87-4b4e-9b7a-624ca1bf832d"),
                    person = 3,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = RequestStatus.PENDING,
                    hrManagerStatus = RequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )
            val vacation1 = testBuilder.manager.vacations.listVacationRequests()

            assertEquals(1, vacation1.size)
            assertTrue(vacation1.find { it.id == UUID.fromString("5c2b0646-8e87-4b4e-9b7a-624ca1bf832d") } != null)
            assertTrue(vacation1.find { it.projectManagerStatus == RequestStatus.PENDING} != null)

            testBuilder.manager.vacations.createVacationRequests(
                vacationRequest = VacationRequest(
                    id = UUID.fromString("5c2b0646-8e87-4b4e-9b7a-624ca1bf832d"),
                    person = 3,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = RequestStatus.APPROVED,
                    hrManagerStatus = RequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()) ,
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[2].atStartOfDay())
                )
            )
            val vacation2 = testBuilder.manager.vacations.listVacationRequests()

            assertEquals(1, vacation2.size)
            assertTrue(vacation2.find { it.projectManagerStatus == RequestStatus.APPROVED} != null)
            assertNotEquals(vacation1 ,vacation2)

            testBuilder.manager.vacations.deleteVacationRequests(
                id = UUID.fromString("5c2b0646-8e87-4b4e-9b7a-624ca1bf832d")
            )
            val vacation3 = testBuilder.manager.vacations.listVacationRequests()

            assertEquals(0, vacation3.size)
        }
    }
}