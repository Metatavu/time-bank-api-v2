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

    val testVacationRequest = VacationRequest(
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

    /**
     * Tests /v1/createVacationRequest -endpoint
     */
    @Test
    fun testCreateVacationRequests() {
        createTestBuilder().use { testBuilder ->
            val createdVacation = testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)
            val vacations = testBuilder.manager.vacationRequests.findVacationRequests(createdVacation.id!!)

            assertEquals(1, vacations.person)
            assertEquals(LocalDate.now().toString(), vacations.startDate)
            assertEquals(LocalDate.now().plusDays(1).toString(), vacations.endDate)
            assertEquals(2, vacations.days)
            assertEquals(VacationType.VACATION, vacations.type)
            assertEquals("Lomaa!!!", vacations.message)
            assertEquals(VacationRequestStatus.PENDING, vacations.projectManagerStatus)
            assertEquals(VacationRequestStatus.PENDING, vacations.hrManagerStatus)
            assertEquals(UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55"), vacations.createdBy)
            assertEquals(UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55"), vacations.lastUpdatedBy)
        }
    }

    /**
     * Tests /v1/listVacationRequest -endpoint
     */
    @Test
    fun testListVacationRequests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val vacations = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            assertEquals(1, vacations[0].person)
            assertEquals(LocalDate.now().toString(), vacations[0].startDate)
            assertEquals(LocalDate.now().plusDays(1).toString(), vacations[0].endDate)
            assertEquals(2, vacations[0].days)
            assertEquals(VacationType.VACATION, vacations[0].type)
            assertEquals("Lomaa!!!", vacations[0].message)
            assertEquals(VacationRequestStatus.PENDING, vacations[0].projectManagerStatus)
            assertEquals(VacationRequestStatus.PENDING, vacations[0].hrManagerStatus)
            assertEquals(UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55"), vacations[0].createdBy)
            assertEquals(UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55"), vacations[0].lastUpdatedBy)
        }
    }

    /**
     * Tests /v1/updateVacationRequest -endpoint
     */
    @Test
    fun testUpdateVacationRequests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.userA.vacationRequests.createVacationRequests(testVacationRequest)
            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            assertEquals(VacationRequestStatus.PENDING, vacations1[0].projectManagerStatus)
            assertEquals(UUID.fromString("7276979e-2f08-4d52-9541-0d10aa3806fe"), vacations1[0].lastUpdatedBy)

            testBuilder.manager.vacationRequests.updateVacationRequests(
                id = vacations1[0].id!!,
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

            val vacations2 = testBuilder.manager.vacationRequests.listVacationRequests()

            assertEquals(VacationRequestStatus.APPROVED, vacations2[0].projectManagerStatus)
            assertEquals(UUID.fromString("50bd84bc-d7f7-445f-b98c-4f6a5d27fb55"), vacations2[0].lastUpdatedBy)
        }
    }

    /**
     * Tests /v1/deleteVacationRequest -endpoint
     */
    @Test
    fun testDeleteVacationRequests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            assertEquals(1, vacations1.size)

            testBuilder.manager.vacationRequests.deleteVacationRequests(vacations1[0].id!!)

            val vacations2 = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            assertEquals(0, vacations2.size)
        }
    }

    /**
     * Tests /v1/deleteVacationRequest -endpoint
     */
    @Test
    fun testUpdateVacationRequestsFail() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)
            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            testBuilder.userA.vacationRequests.assertUpdateFail(401, vacations1[0].id!!, testVacationRequest )
        }
    }

    /**
     * Tests /v1/deleteVacationRequest -endpoint
     */
    @Test
    fun testDeleteVacationRequestsFail() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequests(testVacationRequest)

            val vacations = testBuilder.manager.vacationRequests.listVacationRequests(personId = 1)

            testBuilder.userA.vacationRequests.assertDeleteFail(401, vacations[0].id!!)
        }
    }
}