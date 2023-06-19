package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.VacationRequestStatus
import fi.metatavu.timebank.test.client.models.VacationRequest
import fi.metatavu.timebank.test.client.models.VacationRequestStatuses
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
class VacationRequestStatusTest: AbstractTest() {

    /**
     * Resets Wiremock scenario states before each test
     */
    @BeforeEach
    fun resetScenariosBeforeEach() {
        resetScenarios()
    }

    val testVacationRequest = VacationRequest(
        person = 1,
        startDate = LocalDate.now().toString(),
        endDate = LocalDate.now().plusDays(1).toString(),
        days = 2,
        type = VacationType.VACATION,
        message = "Lomaa!!!",
        createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
        updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
    )

    /**
     * Tests /v1/vacationRequestStatus -endpoint POST method
     */
    @Test
    fun testCreateVacationRequestStatus() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                VacationRequestStatus(
                    person = 123456,
                    vacationRequestId = request.id!!,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val foundStatus = testBuilder.manager.vacationRequestStatus.findVacationRequestStatus(status.id!!)

            assertEquals(123456, foundStatus.person)
            assertEquals(request.id, foundStatus.vacationRequestId)
            assertEquals(VacationRequestStatuses.APPROVED, foundStatus.status)
            assertEquals("Hyväksytty", foundStatus.message)

        }
    }

    /**
     * Tests /v1/vacationRequestStatus -endpoint GET method
     */
    @Test
    fun testListVacationRequestStatus() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                VacationRequestStatus(
                    person = 123456,
                    vacationRequestId = request.id!!,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacationStatuses = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus()

            assertEquals(1, vacationStatuses.size)
            assertEquals(123456, vacationStatuses[0].person)
            assertEquals(request.id, vacationStatuses[0].vacationRequestId)
            assertEquals(VacationRequestStatuses.APPROVED, vacationStatuses[0].status)
            assertEquals("Hyväksytty", vacationStatuses[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint PUT method
     */
    @Test
    fun testUpdateVacationRequestStatus() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                VacationRequestStatus(
                    person = 123456,
                    vacationRequestId = request.id!!,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacationStatuses1 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus()
            assertEquals("Hyväksytty", vacationStatuses1[0].message)

            testBuilder.manager.vacationRequestStatus.updateVacationRequestStatus(
                id = status.id!!,
                vacationRequestStatus = VacationRequestStatus(
                person = 1,
                vacationRequestId = request.id,
                status = VacationRequestStatuses.APPROVED,
                message = "Mene lomalle",
                updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
            ))

            val vacationStatuses2 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus()
            assertEquals("Mene lomalle", vacationStatuses2[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint DELETE method
     */
    @Test
    fun testDeleteVacationRequestStatus() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                VacationRequestStatus(
                    person = 123456,
                    vacationRequestId = request.id!!,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacations1 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus()
            assertEquals(1, vacations1.size)

            testBuilder.manager.vacationRequestStatus.deleteVacationRequestStatus(status.id!!)

            val vacations2 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus()
            assertEquals(0, vacations2.size)
        }
    }
}