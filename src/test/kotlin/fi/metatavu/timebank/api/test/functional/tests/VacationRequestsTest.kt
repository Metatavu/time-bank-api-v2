package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
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
     * Tests /v1/vacationRequest -endpoint POST method
     */
    @Test
    fun testCreateVacationRequests() {
        createTestBuilder().use { testBuilder ->
            val createdVacation = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val vacations = testBuilder.manager.vacationRequests.findVacationRequests(createdVacation.id!!)

            assertEquals(createdVacation.personId, vacations.personId)
            assertEquals(LocalDate.now().toString(), vacations.startDate)
            assertEquals(LocalDate.now().plusDays(1).toString(), vacations.endDate)
            assertEquals(2, vacations.days)
            assertEquals(VacationType.VACATION, vacations.type)
            assertEquals("Lomaa!!!", vacations.message)
        }
    }

    /**
     * Tests /v1/vacationRequest -endpoint GET method
     */
    @Test
    fun testListVacationRequests() {
        createTestBuilder().use { testBuilder ->
            val createdVacation = testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)

            val vacations = testBuilder.manager.vacationRequests.listVacationRequests()

            assertEquals(createdVacation.personId, vacations[0].personId)
            assertEquals(LocalDate.now().toString(), vacations[0].startDate)
            assertEquals(LocalDate.now().plusDays(1).toString(), vacations[0].endDate)
            assertEquals(2, vacations[0].days)
            assertEquals(VacationType.VACATION, vacations[0].type)
            assertEquals("Lomaa!!!", vacations[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequest{id} -endpoint PUT method
     */
    @Test
    fun testUpdateVacationRequests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests()

            testBuilder.manager.vacationRequests.updateVacationRequests(
                id = vacations1[0].id!!,
                vacationRequest = testVacationRequest.copy(message = "Muutoksia lomaan")
            )

            val vacations2 = testBuilder.manager.vacationRequests.listVacationRequests()

            assertEquals("Muutoksia lomaan", vacations2[0].message)
        }
    }

    /**
     * Tests /v1/vacationRequest{id} -endpoint DELETE method
     */
    @Test
    fun testDeleteVacationRequests() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)

            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests()

            assertEquals(1, vacations1.size)

            testBuilder.manager.vacationRequests.deleteVacationRequests(vacations1[0].id!!)

            val vacations2 = testBuilder.manager.vacationRequests.listVacationRequests()

            assertEquals(0, vacations2.size)
        }
    }

    /**
     * Tests Tests /v1/vacationRequest -endpoint PUT method fail
     */
    @Test
    fun testUpdateVacationRequestsFail() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)
            val vacations1 = testBuilder.manager.vacationRequests.listVacationRequests()

            testBuilder.userA.vacationRequests.assertVacationRequestUpdateFail(403, vacations1[0].id!!, VacationRequest(
                personId = testVacationRequest.personId,
                startDate = LocalDate.now().toString(),
                endDate = LocalDate.now().plusDays(1).toString(),
                days = 2,
                type = VacationType.VACATION,
                message = "Lomaa!!!",
                createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
            ) )
        }
    }

    /**
     * Tests /v1/vacationRequest{id} -endpoint DELETE method fail
     */
    @Test
    fun testDeleteVacationRequestsFail() {
        createTestBuilder().use { testBuilder ->
            testBuilder.manager.vacationRequests.createVacationRequest(testVacationRequest)

            val vacations = testBuilder.manager.vacationRequests.listVacationRequests()

            testBuilder.userA.vacationRequests.assertVacationRequestDeleteFail(403, vacations[0].id!!)
        }
    }
}