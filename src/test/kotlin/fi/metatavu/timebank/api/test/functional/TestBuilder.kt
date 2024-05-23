package fi.metatavu.timebank.api.test.functional

import fi.metatavu.jaxrs.test.functional.builder.AbstractAccessTokenTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.AbstractTestBuilder
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.AuthorizedTestBuilderAuthentication
import fi.metatavu.jaxrs.test.functional.builder.auth.InvalidAccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.KeycloakAccessTokenProvider
import fi.metatavu.jaxrs.test.functional.builder.auth.NullAccessTokenProvider
import fi.metatavu.timebank.api.test.functional.auth.TestBuilderAuthentication
import fi.metatavu.timebank.test.client.infrastructure.ApiClient

/**
 * Abstract test builder class
 */
class TestBuilder(private val config: Map<String, String>): AbstractAccessTokenTestBuilder<ApiClient>() {

    val manager = createTestBuilderAuthentication("manager", "test")
    val userA = createTestBuilderAuthentication("test", "password")
    val admin = createTestBuilderAuthentication("admin", "admin")
    val notValid: TestBuilderAuthentication = TestBuilderAuthentication(this, InvalidAccessTokenProvider())
    val userWithNullToken: TestBuilderAuthentication = TestBuilderAuthentication(this, NullAccessTokenProvider())

    override fun createTestBuilderAuthentication(
        abstractTestBuilder: AbstractTestBuilder<ApiClient, AccessTokenProvider>,
        authProvider: AccessTokenProvider
    ): AuthorizedTestBuilderAuthentication<ApiClient, AccessTokenProvider> {
        return TestBuilderAuthentication(this, authProvider)
    }
    /**
     * Creates test builder authenticator for given user
     *
     * @param username username
     * @param password password
     * @return test builder authenticator for given user
     */
    private fun createTestBuilderAuthentication(username: String, password: String): TestBuilderAuthentication {
        val serverUrl = config.getValue("quarkus.oidc.auth-server-url").substringBeforeLast("/").substringBeforeLast("/")
        val realm = config.getValue("quarkus.oidc.auth-server-url").substringAfterLast("/")
        val clientId = "test"
        return TestBuilderAuthentication(this, KeycloakAccessTokenProvider(serverUrl, realm, clientId, username, password, null))
    }
}