package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.VacationRequestStatus
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
        val id = UUID.fromString("5c2b0646-8e87-4b4e-9b7a-624ca1bf832d")
            createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequests(
                vacationRequest = VacationRequest(
                    id = id,
                    person = 3,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = VacationRequestStatus.PENDING,
                    hrManagerStatus = VacationRequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    createdBy = UUID.randomUUID(),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    lastModifiedBy = UUID.randomUUID()
                )
            )
            var vacations = testBuilder.manager.vacationRequests.listVacationRequests(personId = 3, after = LocalDate.now().toString())

            assertEquals(1, vacations.size)
            assertTrue(vacations.find { it.id == id } != null)
            assertTrue(vacations.find { it.projectManagerStatus == VacationRequestStatus.PENDING} != null)

            testBuilder.manager.vacationRequests.updateVacationRequests(
                id = id,
                vacationRequest = VacationRequest(
                    id = id,
                    person = 3,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = VacationRequestStatus.APPROVED,
                    hrManagerStatus = VacationRequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    createdBy = UUID.randomUUID(),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[2].atStartOfDay()),
                    lastModifiedBy = UUID.randomUUID()
                )
            )
            vacations = testBuilder.manager.vacationRequests.listVacationRequests(personId = 3, after = LocalDate.now().toString())

            assertEquals(1, vacations.size)
            assertTrue(vacations.find { it.id == id } != null)
            assertTrue(vacations.find { it.projectManagerStatus == VacationRequestStatus.APPROVED } != null)

            vacations.forEach { vacation ->
                testBuilder.manager.vacationRequests.clean(vacation)
            }
        }
    }
}