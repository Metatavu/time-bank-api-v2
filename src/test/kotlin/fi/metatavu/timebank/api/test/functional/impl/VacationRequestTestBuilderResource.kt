package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.VacationRequestsApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.models.VacationRequest
import org.junit.Assert
import java.util.*

/**
 * Test builder resource for VacationRequests API
 */
class VacationRequestTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<VacationRequest, ApiClient?>(testBuilder, apiClient) {

    override fun clean(v: VacationRequest) {
        api.deleteVacationRequest(v.id!!)
    }

    override fun getApi(): VacationRequestsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return VacationRequestsApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Create a new VacationRequest
     *
     * @param vacationRequest vacationRequest body
     * @return Created vacationRequest
     */
    fun createVacationRequest(vacationRequest: VacationRequest): VacationRequest {
        return addClosable(api.createVacationRequest(vacationRequest))
    }

    /**
     * List all VacationRequest
     *
     * @param personId optional personId
     * @param before optional before date
     * @param after optional after date
     * @return List of VacationRequests
     */
    fun listVacationRequests(personId: UUID? = null, before: String? = null, after: String? = null): Array<VacationRequest> {
        return api.listVacationRequests(
            personId = personId,
            before = before,
            after = after
        )
    }

    /**
     * Find a VacationRequest
     *
     * @param id id of the VacationRequest
     * @return found VacationRequests
     */
    fun findVacationRequests(id: UUID): VacationRequest {
        return api.findVacationRequest(id)
    }

    /**
     * Update VacationRequest
     *
     * @param id id of vacationRequest being updated
     * @param vacationRequest updated vacationRequest
     * @return Updated vacationRequest
     */
    fun updateVacationRequests(id: UUID, vacationRequest: VacationRequest): VacationRequest {
        return api.updateVacationRequest(
            id = id,
            vacationRequest = vacationRequest
        )
    }

    /**
     * Removes closable
     *
     * @param vacationRequestId id of the vacationRequest
     */
    private fun remove(vacationRequestId: UUID) {
        removeCloseable { closable ->
            if (closable !is VacationRequest) {
                return@removeCloseable false
            }

            closable.id!! == vacationRequestId
        }
    }

    /**
     * Delete persisted VacationRequest
     *
     * @param id id of the vacationRequest
     */
    fun deleteVacationRequests(id: UUID) {
        api.deleteVacationRequest(id = id)
        remove(id)
    }

    /**
     * Asserts that deleting VacationRequest fails with given status
     *
     * @param expectedStatus expected status code
     * @param id entryId
     */
    fun assertVacationRequestDeleteFail(expectedStatus: Int, id: UUID) {
        try {
            api.deleteVacationRequest(id = id)
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that updating VacationRequest fails with given status
     *
     * @param expectedStatus expected status code
     * @param id requestId
     * @param id Updated VacationRequest
     */
    fun assertVacationRequestUpdateFail(expectedStatus: Int, id: UUID, vacationRequest: VacationRequest) {
        try {
            api.updateVacationRequest(id, vacationRequest)
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}