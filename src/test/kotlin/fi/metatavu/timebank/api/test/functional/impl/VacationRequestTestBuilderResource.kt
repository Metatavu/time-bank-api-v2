package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.VacationsApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.models.VacationRequest

/**
 * Test builder resource for Vacations API
 */
class VacationRequestTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<VacationRequest, ApiClient?>(testBuilder, apiClient) {
    override fun clean(t: VacationRequest?) {

    }


    override fun getApi(): VacationsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return VacationsApi(ApiTestSettings.apiBasePath)
    }

    /**
     * List all vacation requests
     *
     * @param personId optional personId
     * @param before optional before date
     * @param after optional after date
     */
    fun getVacationRequests(personId: Int? = null, before: String? = null, after: String? = null): Array<VacationRequest> {
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
     */
    fun newVacationRequests(vacationRequest: VacationRequest) {
        return api.createVacationRequest(
            vacationRequest = vacationRequest
        )

    }
}