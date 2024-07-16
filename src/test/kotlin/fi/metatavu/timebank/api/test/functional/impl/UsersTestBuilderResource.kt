package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.UsersApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.models.User
import org.junit.Assert
import java.util.*

/**
 * Resource for testing Users API
 */
class UsersTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<User, ApiClient?>(testBuilder, apiClient) {
    override fun clean(p0: User?) {
    }

    override fun getApi(): UsersApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return UsersApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Gets all users
     *
     * @return Array of Users
     */
    fun getUsers(): Array<User>{
        return api.listUsers()
    }

    /**
     * Finds user based on UUID
     *
     * @return User
     */
    fun findUser(userId: UUID): User {
        return api.findUser(userId)
    }

    /**
     * Asserts that listing users with a null token fails with given status
     *
     * @param expectedStatus expected status
     */
    fun assertListFailWithStatus(expectedStatus: Int) {
        try {
            api.listUsers()
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}