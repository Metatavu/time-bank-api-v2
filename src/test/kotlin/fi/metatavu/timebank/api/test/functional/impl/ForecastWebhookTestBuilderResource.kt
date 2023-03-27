package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.ForecastWebhookApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.models.ForecastWebhookEvent
import fi.metatavu.timebank.test.client.models.TimeEntry

/**
 * Test builder resource for TimeEntries API
 */
class ForecastWebhookTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<TimeEntry, ApiClient?>(testBuilder, apiClient) {

    override fun clean(t: TimeEntry) {
    }

    override fun getApi(): ForecastWebhookApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ForecastWebhookApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Test forecast webhook
     *
     * @param forecastWebhookEvent test webhook content
     * @param forecastWebhookKey Test key to authenticate incoming webhooks
     */
    fun forecastWebhook(forecastWebhookEvent: ForecastWebhookEvent, forecastWebhookKey: String) {
        return api.forecastWebhook(
            forecastWebhookEvent = forecastWebhookEvent,
            forecastWebhookKey = forecastWebhookKey
        )
    }
}

