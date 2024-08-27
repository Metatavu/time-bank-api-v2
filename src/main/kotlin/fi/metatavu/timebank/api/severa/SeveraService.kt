package fi.metatavu.timebank.api.severa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.severa.models.SeveraUser
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SeveraService {

    @ConfigProperty(name = "severa.demo.base.url")
    lateinit var severaBaseUrl: String

    @ConfigProperty(name = "severa.demo.client.id")
    lateinit var severaClientId: String

    @Inject
    lateinit var severaAccessTokenContainer: SeveraAccessTokenContainer

    @Inject
    lateinit var logger: Logger

    /**
     * Sends get request to Severa API
     *
     * @param path Path for the request
     * @param scopes List of scopes
     * @return Response from the request
     */
    private inline fun <reified T> doRequest(path: String, scopes: List<String>): T? {
        return try {
            val client = OkHttpClient()
            val accessToken = severaAccessTokenContainer.getAccessToken(scopes).accessToken
            val request = Request.Builder().url("${severaBaseUrl}${path}")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Client_id", severaClientId)
                .build()
            val response = client.newCall(request).execute()
            when (response.code()) {
                200 -> jacksonObjectMapper().readValue(response.body()?.string(), T::class.java)
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
    fun getUsers(): List<SeveraUser> {
        val response = doRequest<Array<SeveraUser>>("/v1/users", listOf(USERS_READ))
        if (response != null) {
            return response.toList()
        } else {
            logger.error("getUsers(): Request failed or returned null")
            return emptyList()
        }
    }

    /**
     * Finds user from Severa
     *
     * @return List of ForecastPersons
     */
    fun findUser(guid: String): SeveraUser {
        return doRequest<SeveraUser>("/v1/users/$guid", listOf(USERS_READ)) ?: SeveraUser()
    }

    companion object {
        const val USERS_READ = "users:read"
    }
}