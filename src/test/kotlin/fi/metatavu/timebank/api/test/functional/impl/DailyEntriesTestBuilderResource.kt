package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.DailyEntriesApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.models.DailyEntry
import org.junit.Assert

class DailyEntriesTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<DailyEntry, ApiClient?>(testBuilder, apiClient) {

    override fun clean(t: DailyEntry?) {
        TODO("Not yet implemented")
    }


    override fun getApi(): DailyEntriesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return DailyEntriesApi(ApiTestSettings.apiBasePath)
    }

    fun getDailyEntries(personId: Int?, before: String?, after: String?): Array<DailyEntry> {
        return api.listDailyEntries(
            personId = personId,
            before = before,
            after = after
        )
    }

    fun assertListFail(expectedStatus: Int, id: Int?, before: String?, after: String?) {
        try{
            api.listDailyEntries(
                personId = id,
                before = before,
                after = after
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    fun assertListFailWithNullToken(expectedStatus: Int) {
        try {
            api.listDailyEntries(
                personId = null,
                before = null,
                after = null
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}