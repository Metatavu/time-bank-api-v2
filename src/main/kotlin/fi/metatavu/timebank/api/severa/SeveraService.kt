package fi.metatavu.timebank.api.severa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.severa.models.SeveraFlextime
import fi.metatavu.timebank.api.severa.models.SeveraWorkhourResponse
import fi.metatavu.timebank.model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.inject.Inject

class SeveraService {
    @ConfigProperty(name = "severa.base.url")
    lateinit var severaBaseUrl: String

    @ConfigProperty(name = "severa.client.id")
    lateinit var severaClientId: String

    @ConfigProperty(name = "severa.client.secret")
    lateinit var severaClientSecret: String

    lateinit var bearerToken: String

    @Inject
    lateinit var logger: Logger

    /**
     * Sends get request to Severa API
     *
     * @param path path for the request
     * @return Response from the request
     */
    private fun doRequest(path: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url("${severaBaseUrl}${path}")
                .addHeader("Authorization", "Bearer $bearerToken")
                .addHeader("Client_id", severaClientId)
                .addHeader("Client_secret", severaClientSecret)
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
            doRequest("/v1/users"),
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
            doRequest("/v1/users/$guid"),
            User::class.java
        )
    }

    /**
     * Gets work hours from Severa
     *
     * @param startDate starting date for query
     * @param endDate ending date for query
     * @param pageNumber page of paginated response to request
     *
     * @return SeveraTimeEntryResponse
     */
    fun getWorkhoursForUser(startDate: String?, endDate: String?, pageNumber: Int, guid: String): SeveraWorkhourResponse {
        val pathSections = mutableListOf<String>()
        pathSections.add("/v1/users/$guid/workhours")
        if (startDate != null) {
            pathSections.add("?startDate=$startDate")
        }
        if (endDate != null) {
            pathSections.add("?endDate=$endDate")
        }
        pathSections.add("?pageSize=1000&pageNumber=$pageNumber")

        return jacksonObjectMapper().readValue(
            doRequest(pathSections.joinToString("")),
            SeveraWorkhourResponse::class.java
        )
    }

    fun getUserFlextimeBalance(guid: String): SeveraFlextime {
        return jacksonObjectMapper().readValue(
            doRequest("/v1/users/$guid/flextime"),
            SeveraFlextime::class.java
        )
    }
}