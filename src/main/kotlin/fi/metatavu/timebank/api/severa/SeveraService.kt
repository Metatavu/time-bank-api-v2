package fi.metatavu.timebank.api.severa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.severa.models.SeveraAccessToken
import fi.metatavu.timebank.model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.enterprise.context.RequestScoped
import javax.inject.Inject

@RequestScoped
class SeveraService {

    @ConfigProperty(name = "severa.base.url")
    lateinit var severaBaseUrl: String

    @ConfigProperty(name = "severa.client.id")
    lateinit var severaClientId: String

    @Inject
    lateinit var severaAccessTokenContainer: SeveraAccessTokenContainer

    @Inject
    lateinit var logger: Logger

    /**
     * Sends get request to Severa API
     *
     * @param path path for the request
     * @return Response from the request
     */
    private fun doRequest(path: String, scope: String): String? {
        return try {
            val client = OkHttpClient()
            val accessToken = severaAccessTokenContainer.getAccessToken(scope).accessToken
            val request = Request.Builder().url("${severaBaseUrl}${path}")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Client_id", severaClientId)
                .build()
            val response = client.newCall(request).execute()
            when (response.code()) {
                200 -> response.body()?.string()
                else -> throw Error("Couldn't reach Severa API.")
            }
        } catch (e: Error) {
            logger.error("Error when executing get request: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Gets users from Severa
     *
     * @return List of Users
     */
    fun getUsers(): List<User> {
        return jacksonObjectMapper().readValue(
            doRequest("/v1/users", USERS_READ),
            Array<User>::class.java
        ).toList()
    }

    /**
     * Finds user from Severa
     *
     * @return List of ForecastPersons
     */
    fun findUser(guid: String): User {
        return jacksonObjectMapper().readValue(
            doRequest("/v1/users/$guid", "users:read"),
            User::class.java
        )
    }

    companion object {
        const val USERS_READ = "users:read"
    }
}