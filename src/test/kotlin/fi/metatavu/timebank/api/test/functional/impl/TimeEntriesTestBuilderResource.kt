package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.TimeEntriesApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.models.ForecastDeleteWebhookEvent
import fi.metatavu.timebank.test.client.models.TimeEntry
import org.junit.Assert
import java.util.UUID

/**
 * Test builder resource for TimeEntries API
 */
class TimeEntriesTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<TimeEntry, ApiClient?>(testBuilder, apiClient) {

    override fun clean(t: TimeEntry) {
        api.deleteTimeEntry(t.id)
    }

    override fun getApi(): TimeEntriesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return TimeEntriesApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Lists all TimeEntries
     *
     * @param personId optional personId
     * @param before optional before date
     * @param after optional after date
     * @param vacation optional vacation filter
     * @return List of TimeEntries
     */
    fun getTimeEntries(personId: Int? = null, before: String? = null, after: String? = null, vacation: Boolean? = null): Array<TimeEntry> {
        return api.listTimeEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = vacation
        )
    }

    /**
     * delete time entry
     *
     * @param forecastDeleteWebhookEvent test webhook content
     */

    fun forecastTimeEntriesDeleteWebhook(forecastDeleteWebhookEvent: ForecastDeleteWebhookEvent, forecastDeleteWebhookKey: String) {
        return api.forecastTimeEntriesDeleteWebhook(
                forecastDeleteWebhookEvent = forecastDeleteWebhookEvent,
                forecastDeleteWebhookKey = forecastDeleteWebhookKey
        )
    }

    /**
     * Asserts that deleting timeEntry fails with given status
     *
     * @param expectedStatus expected status code
     * @param id entryId
     */
    fun assertDeleteFail(expectedStatus: Int, id: UUID) {
        try {
            api.deleteTimeEntry(id = id)
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}