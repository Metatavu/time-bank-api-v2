package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.VacationRequestStatusApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.models.VacationRequest
import fi.metatavu.timebank.test.client.models.VacationRequestStatus
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
        api.deleteVacationRequestStatus(v.id!!)
    }

    override fun getApi(): VacationRequestStatusApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return VacationRequestStatusApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Create a new VacationRequestStatus
     *
     * @param status VacationRequestStatus body
     * @return Created vacationRequest
     */
    fun createVacationRequestStatus(status: VacationRequestStatus): VacationRequestStatus {
        return addClosable(api.createVacationRequestStatus(status))
    }

    /**
     * List all VacationRequestStatuses
     *
     * @param vacationRequestId optional id of VacationRequest
     * @param personId optional personId
     * @return List of VacationRequests
     */
    fun listVacationRequestStatus(vacationRequestId: UUID? = null, personId: Int? = null): Array<VacationRequestStatus> {
        return api.listVacationRequestStatuses(vacationRequestId = vacationRequestId, personId = personId)
    }

    /**
     * Find a VacationRequestStatus
     *
     * @param id id of the VacationRequestStatus
     * @return found VacationRequestStatus
     */
    fun findVacationRequestStatus(id: UUID): VacationRequestStatus {
        return api.findVacationRequestStatus(id)
    }

    /**
     * Update VacationRequestStatus
     *
     * @param id id of vacationRequestStatus being updated
     * @param vacationRequestStatus updated vacationRequestStatus
     * @return Updated vacationRequestStatus
     */
    fun updateVacationRequestStatus(id: UUID, vacationRequestStatus: VacationRequestStatus): VacationRequestStatus {
        return api.updateVacationRequestStatus(id = id, vacationRequestStatus = vacationRequestStatus)
    }

    /**
     * Removes closable
     *
     * @param vacationRequestStatusId id of the vacationRequest
     */
    private fun remove(vacationRequestStatusId: UUID) {
        removeCloseable { closable ->
            if (closable !is VacationRequest) {
                return@removeCloseable false
            }

            closable.id!! == vacationRequestStatusId
        }
    }

    /**
     * Delete persisted VacationRequestStatus
     *
     * @param id id of the vacationRequestStatus
     */
    fun deleteVacationRequestStatus(id: UUID) {
        api.deleteVacationRequestStatus(id)
        remove(id)
    }


}

