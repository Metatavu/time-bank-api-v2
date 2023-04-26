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
        createTestBuilder().use { testBuilder ->
            testBuilder.userA.vacationRequests.createVacationRequests(
                vacationRequest = VacationRequest(
                    person = 1,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = VacationRequestStatus.PENDING,
                    hrManagerStatus = VacationRequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                )
            )
            var vacations = testBuilder.manager.vacationRequests.listVacationRequests(
                personId = 1,
                after = LocalDate.now().toString()
            )

            val id = vacations[0].id!!

            assertEquals(1, vacations.size)
            assertTrue(vacations.find { it.id == id } != null)
            assertTrue(vacations.find { it.projectManagerStatus == VacationRequestStatus.PENDING} != null)
            assertTrue(vacations.find { it.lastModifiedBy == UUID.fromString("7276979e-2f08-4d52-9541-0d10aa3806fe") } != null)

            testBuilder.manager.vacationRequests.updateVacationRequests(
                id = id,
                vacationRequest = VacationRequest(
                    person = 1,
                    startDate = LocalDate.now().toString(),
                    endDate = LocalDate.now().plusDays(1).toString(),
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = VacationRequestStatus.APPROVED,
                    hrManagerStatus = VacationRequestStatus.PENDING,
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[2].atStartOfDay()),
                )
            )
            vacations = testBuilder.manager.vacationRequests.listVacationRequests(
                personId = 1,
                after = LocalDate.now().toString()
            )

            assertEquals(1, vacations.size)
            assertTrue(vacations.find { it.id == id } != null)
            assertTrue(vacations.find { it.projectManagerStatus == VacationRequestStatus.APPROVED } != null)
            assertTrue(vacations.find { it.hrManagerStatus == VacationRequestStatus.PENDING } != null)
            assertTrue(vacations.find { it.lastModifiedBy == UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55") } != null)

            vacations.forEach { vacation ->
                testBuilder.manager.vacationRequests.clean(vacation)
            }
        }
    }
}