package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import fi.metatavu.timebank.test.client.models.VacationRequest
import fi.metatavu.timebank.test.client.models.VacationRequestStatus
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
    personId = UUID.randomUUID(),
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
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val foundStatus = testBuilder.manager.vacationRequestStatus.findVacationRequestStatus(request.id, status.id!!)

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
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)

            testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacationStatuses = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(1, vacationStatuses.size)
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
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacationStatuses1 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(status.id, vacationStatuses1[0].id)
            assertEquals("Hyväksytty", vacationStatuses1[0].message)

            testBuilder.manager.vacationRequestStatus.updateVacationRequestStatus(
                requestId = request.id,
                statusId = status.id!!,
                vacationRequestStatus = VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Mene lomalle",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                )
            )

            val vacationStatuses2 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(status.id, vacationStatuses2[0].id)
            assertEquals("Mene lomalle", vacationStatuses2[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint PUT method permissions
     */
    @Test
    fun testUpdateVacationRequestStatusPermissions() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.PENDING,
                    message = "Olen manager ja loin statuksen",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacationStatuses1 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(status.id, vacationStatuses1[0].id)
            assertEquals("Olen manager ja loin statuksen", vacationStatuses1[0].message)

            testBuilder.admin.vacationRequestStatus.updateVacationRequestStatus(
                requestId = request.id,
                statusId = status.id!!,
                vacationRequestStatus = VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Olen admin ja muokkasin managerin statusta",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                )
            )

            val vacationStatuses2 = testBuilder.admin.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(status.id, vacationStatuses2[0].id)
            assertEquals("Olen admin ja muokkasin managerin statusta", vacationStatuses2[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint DELETE method
     */
    @Test
    fun testDeleteVacationRequestStatus() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            val vacations1 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)
            assertEquals(1, vacations1.size)

            testBuilder.manager.vacationRequestStatus.deleteVacationRequestStatus(request.id, status.id!!)

            val vacations2 = testBuilder.manager.vacationRequestStatus.listVacationRequestStatus(request.id)

            assertEquals(0, vacations2.size)
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint PUT method fail
     */
    @Test
    fun testUpdateVacationRequestStatusFail() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)

            testBuilder.manager.vacationRequests.listVacationRequests()

            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            testBuilder.userA.vacationRequestStatus.assertVacationStatusUpdateFail(
                403,
                requestId = request.id,
                statusId = status.id!!,
                vacationRequestStatus = VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Mene lomalle",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                )
            )
        }
    }

    /**
     * Tests /v1/vacationRequestStatus{id} -endpoint DELETE method fail
     */
    @Test
    fun testDeleteVacationRequestStatusFail() {
        createTestBuilder().use { testBuilder ->
            val request = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val status = testBuilder.manager.vacationRequestStatus.createVacationRequestStatus(
                requestId = request.id!!,
                VacationRequestStatus(
                    vacationRequestId = request.id,
                    status = VacationRequestStatuses.APPROVED,
                    message = "Hyväksytty",
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                )
            )

            testBuilder.userA.vacationRequestStatus.assertVacationStatusDeleteFail(403, request.id, status.id!!)
        }
    }
}