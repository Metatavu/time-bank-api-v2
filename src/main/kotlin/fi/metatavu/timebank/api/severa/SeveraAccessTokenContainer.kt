package fi.metatavu.timebank.api.severa

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.severa.models.SeveraAccessToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SeveraAccessTokenContainer {
    @ConfigProperty(name = "severa.base.url")
    lateinit var severaBaseUrl: String

    @ConfigProperty(name = "severa.client.id")
    lateinit var severaClientId: String

    @ConfigProperty(name = "severa.client.secret")
    lateinit var severaClientSecret: String

    @Inject
    lateinit var logger: Logger

    private lateinit var severaAccessToken: SeveraAccessToken

    /**
     * Determines if a new access token is needed. Returns a functional access token for Severa API calls
     *
     * @param scopes List of scopes
     * @return Access token for Severa API calls
     */
    fun getAccessToken(scopes: List<String>): SeveraAccessToken{
        return if (isValidToken(severaAccessToken.accessToken) && containsScopes(scopes)){
            severaAccessToken
        } else {
            getNewAccessToken(scopesToString(scopes))
        }
    }

    /**
     * Converts a list of scopes to be a suitable string for requesting an access token
     *
     * @param scopes List of scopes
     * @return String
     */
    private fun scopesToString(scopes: List<String>): String{
        val scopeBuilder = StringBuilder()
        for (scope in scopes){
            scopeBuilder.append(scope).append(", ")
        }

        return scopeBuilder.toString()
    }

    /**
     * Checks if potentially existing access token has the needed scope(s)
     *
     * @param scopes List of scopes
     * @return Boolean
     */
    private fun containsScopes(scopes: List<String>): Boolean {
        for (scope in scopes){
            if (!severaAccessToken.scope.contains(scope)) return false
        }
        return true
    }

    /**
     * Checks if an access token is valid. Does NOT check for suitable scopes.
     *
     * @param token accessToken
     * @return boolean
     */
    private fun isValidToken(token: String): Boolean{
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url("${severaBaseUrl}/heartbeat/authorized")
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Client_id", severaClientId)
                .build()
            val response = client.newCall(request).execute()
            when (response.code()) {
                204 -> true
                else -> false
            }
        } catch (e: Error) {
            logger.error("Error when executing get request: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Requests Severa API to generate a fresh access token
     *
     * @param scope Scope
     * @return Access token for Severa API calls
     */
    private fun getNewAccessToken(scope: String): SeveraAccessToken{
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
                200 -> parseAccessTokenFromJson(response.body().toString())
                else -> throw Error("Couldn't reach Severa API.")
            }
        } catch (e: Error) {
            logger.error("Error when generating new access token: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Parses access token from response body
     *
     * @param responseBody responseBody
     * @return Bearer token for Severa API calls
     */
    fun parseAccessTokenFromJson(responseBody: String?): SeveraAccessToken{
        return try {
            jacksonObjectMapper().readValue(responseBody, SeveraAccessToken::class.java)
        } catch (e: Exception) {
            throw Error("Error when parsing bearer token from JSON: ${e.localizedMessage}")
        }
    }
}