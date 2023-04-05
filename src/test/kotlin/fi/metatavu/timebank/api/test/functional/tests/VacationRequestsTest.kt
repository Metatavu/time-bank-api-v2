package fi.metatavu.timebank.api.test.functional.tests

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

            testBuilder.manager.vacations.newVacationRequests(
                vacationRequest = VacationRequest(
                    person = 2,
                    startDate = "2023-03-27",
                    endDate = "2023-03-28",
                    days = 2,
                    type = VacationType.VACATION,
                    message = "Lomaa!!!",
                    projectManagerStatus = RequestStatus.APPROVED,
                    hrManagerStatus = RequestStatus.PENDING,
                    createdAt = "2023-03-24T10:15:30+03:00",
                    updatedAt = "2023-03-24T10:15:30+03:00"
                )
            )

            val requests = testBuilder.manager.vacations.getVacationRequests()

            requests.forEach { println(it) }
        }
    }
}