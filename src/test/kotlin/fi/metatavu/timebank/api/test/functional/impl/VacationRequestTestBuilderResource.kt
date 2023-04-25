package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.VacationRequestsApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.models.VacationRequest
import java.util.*

/**
 * Test builder resource for Vacations API
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
        return VacationRequestsApi (ApiTestSettings.apiBasePath)
    }

    /**
     * List all vacation requests
     *
     * @param personId optional personId
     * @param before optional before date
     * @param after optional after date
     * @return List of VacationRequests
     */
    fun listVacationRequests(personId: Int? = null, before: String? = null, after: String? = null): Array<VacationRequest> {
        return api.listVacationRequests(
            personId = personId,
            before =  before,
            after = after
        )
    }

    /**
     * Create a new vacation request
     *
     * @param vacationRequest vacationRequest body
     * @return Created vacationRequest
     */
    fun createVacationRequests(vacationRequest: VacationRequest): VacationRequest {
        return api.createVacationRequest(vacationRequest)
    }

    /**
     * Create a new vacation request
     *
     * @param vacationRequest vacationRequest body
     * @return Created vacationRequest
     */
    fun updateVacationRequests(id: UUID, vacationRequest: VacationRequest): VacationRequest {
        return api.updateVacationRequest(id, vacationRequest)
    }
}