package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.VacationRequestStatusApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.models.VacationRequestStatus
import org.junit.Assert
import java.util.*

/**
 * Test builder resource for VacationRequestStatus API
 */
class VacationRequestStatusTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<VacationRequestStatus, ApiClient?>(testBuilder, apiClient) {

    override fun clean(v: VacationRequestStatus) {
        api.deleteVacationRequestStatus(v.vacationRequestId!!, v.id!!)
    }

    override fun getApi(): VacationRequestStatusApi{
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return VacationRequestStatusApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Create a new VacationRequestStatus
     *
     * @param requestId Id of VacationRequest related to the status
     * @param status VacationRequestStatus body
     * @return Created vacationRequest
     */
    fun createVacationRequestStatus(requestId: UUID, status: VacationRequestStatus): VacationRequestStatus {
        return addClosable(api.createVacationRequestStatus(requestId, status))
    }

    /**
     * List all VacationRequestStatuses
     *
     * @param requestId Id of VacationRequest related to the status
     * @return List of VacationRequests
     */
    fun listVacationRequestStatus(requestId: UUID): Array<VacationRequestStatus> {
        return api.listVacationRequestStatuses(requestId)
    }

    /**
     * Find a VacationRequestStatus
     *
     * @param requestId Id of VacationRequest related to the status
     * @param statusId id of vacationRequestStatus
     * @return found VacationRequestStatus
     */
    fun findVacationRequestStatus(requestId: UUID, statusId: UUID): VacationRequestStatus {
        return api.findVacationRequestStatus(requestId, statusId)
    }

    /**
     * Update VacationRequestStatus
     *
     * @param requestId Id of VacationRequest related to the status
     * @param statusId id of vacationRequestStatus being updated
     * @param vacationRequestStatus updated vacationRequestStatus
     * @return Updated vacationRequestStatus
     */
    fun updateVacationRequestStatus(requestId: UUID, statusId: UUID, vacationRequestStatus: VacationRequestStatus): VacationRequestStatus {
        return api.updateVacationRequestStatus(
            id = requestId,
            statusId = statusId,
            vacationRequestStatus = vacationRequestStatus)
    }

    /**
     * Removes closable
     *
     * @param vacationRequestStatusId id of the vacationRequest
     */
    private fun remove(vacationRequestStatusId: UUID) {
        removeCloseable { closable ->
            if (closable !is VacationRequestStatus) {
                return@removeCloseable false
            }

            closable.id!! == vacationRequestStatusId
        }
    }

    /**
     * Delete persisted VacationRequestStatus
     *
     * @param requestId Id of VacationRequest related to the status
     * @param statusId id of vacationRequestStatus being updated
     */
    fun deleteVacationRequestStatus(requestId: UUID, statusId: UUID) {
        api.deleteVacationRequestStatus(requestId, statusId)
        remove(statusId)
    }

    /**
     * Asserts that deleting VacationRequest fails with given status
     *
     * @param expectedStatus expected status code
     * @param requestId Id of VacationRequest related to the status
     * @param statusId id of vacationRequestStatus being updated
     */
    fun assertVacationStatusDeleteFail(expectedStatus: Int, requestId: UUID, statusId: UUID) {
        try {
            api.deleteVacationRequestStatus(requestId, statusId)
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that updating VacationRequest fails with given status
     *
     * @param expectedStatus expected status code
     * @param requestId Id of VacationRequest related to the status
     * @param statusId id of vacationRequestStatus being updated
     * @param vacationRequestStatus updated vacationRequestStatus
     */
    fun assertVacationStatusUpdateFail(expectedStatus: Int, requestId: UUID, statusId: UUID, vacationRequestStatus: VacationRequestStatus) {
        try {
            api.updateVacationRequestStatus(requestId, statusId, vacationRequestStatus)
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}

