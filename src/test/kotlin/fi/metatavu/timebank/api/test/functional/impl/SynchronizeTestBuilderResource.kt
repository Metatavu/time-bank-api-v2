package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.SynchronizeApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.models.TimeEntry

/**
 * Test builder resource for Synchronize API
 */
class SynchronizeTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<TimeEntry, ApiClient?>(testBuilder, apiClient) {

    // This test builder resource doesn't clean anything therefore this is empty.
    override fun clean(t: TimeEntry?) {
    }

    override fun getApi(): SynchronizeApi {
        super.getApiClient()
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return SynchronizeApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Synchronizes time entries
     *
     * @param before before date
     * @param after after date
     */
    fun synchronizeEntries(before: String? = null, after: String? = null) {
        api.synchronizeTimeEntries(
            before = before,
            after = after
        )
    }

    /**
     * Synchronizes deleted time entries
     *
     * @param before before date
     * @param after after date
     */
    fun synchronizeDeletedEntries(before: String? = null, after: String? = null) {
        api.synchronizeDeletedTimeEntries(
            before = before,
            after = after
        )
    }
}