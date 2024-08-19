package fi.metatavu.timebank.api.severa

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import fi.metatavu.timebank.api.severa.models.SeveraBearerToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SeveraBearerTokenContainer {
    @ConfigProperty(name = "severa.base.url")
    lateinit var severaBaseUrl: String

    @ConfigProperty(name = "severa.client.id")
    lateinit var severaClientId: String

    @ConfigProperty(name = "severa.client.secret")
    lateinit var severaClientSecret: String

    @Inject
    lateinit var logger: Logger

    /**
     * Generates a new bearer token for Severa API calls
     *
     * @param scope Scope
     * @return Bearer token for Severa API calls
     */
    fun getNewBearerToken(scope: String): SeveraBearerToken{
        return try {
            val client = OkHttpClient()
            val requestBody = FormBody.Builder()
                .add("client_id", severaClientId)
                .add("client_secret", severaClientSecret)
                .add("scope", scope)
                .build()
            val request = Request.Builder().url("$severaBaseUrl/v1/token")
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()
            when (response.code()) {
                200 -> parseBearerTokenFromJson(response.body().toString())
                else -> throw Error("Couldn't reach Severa API.")
            }
        } catch (e: Error) {
            logger.error("Error when executing get request: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Parses Bearer token from response body
     *
     * @param responseBody responseBody
     * @return Bearer token for Severa API calls
     */
    fun parseBearerTokenFromJson(responseBody: String?): SeveraBearerToken{
        return try {
            val gson = Gson()
            gson.fromJson(responseBody, SeveraBearerToken::class.java)
        } catch (e: JsonSyntaxException) {
            throw Error("Error when parsing bearer token from JSON: ${e.localizedMessage}")
        }
    }
}