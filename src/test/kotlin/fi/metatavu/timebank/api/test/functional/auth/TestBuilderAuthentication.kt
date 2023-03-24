package fi.metatavu.timebank.api.test.functional.auth

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenTestBuilderAuthentication
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.impl.*
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.infrastructure.ApiClient

/**
 * Test builder authentication
 *
 * @author Jari Nykänen
 *
 * @param testBuilder test builder instance
 * @param accessTokenProvider access token provider
 */
class TestBuilderAuthentication(
    private val testBuilder: TestBuilder,
    accessTokenProvider: AccessTokenProvider
): AccessTokenTestBuilderAuthentication<ApiClient>(testBuilder, accessTokenProvider) {

    private var accessTokenProvider: AccessTokenProvider? = accessTokenProvider

    val persons = PersonsTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val synchronization = SynchronizeTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val timeEntries = TimeEntriesTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val dailyEntries = DailyEntriesTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())
    val forecastWebhooks = ForecastWebhookTestBuilderResource(testBuilder, this.accessTokenProvider, createClient())

    /**
     * Creates an API client
     *
     * @param authProvider access token
     * @return API client
     */
    override fun createClient(authProvider: AccessTokenProvider): ApiClient {
        val result = ApiClient(ApiTestSettings.apiBasePath)
        ApiClient.accessToken = authProvider.accessToken
        return result
    }
}