package fi.metatavu.timebank.api.slack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class SlackController {

    @Inject
    lateinit var logger: Logger

    @ConfigProperty(name = "slack.vacation.requests.message.endpoint")
    lateinit var slackVacationRequestsMessageEndpoint: String

    /**
     * Sends a Slack message to managers. Return true/false depending on success
     *
     * @param message String
     * @return Boolean
     */
    suspend fun messageManagers(message: String) {
        try {
            OkHttpClient()
                .newCall(
                    Request.Builder()
                        .url(slackVacationRequestsMessageEndpoint)
                        .put(
                            RequestBody.create(
                            MediaType.parse("application/json"),
                            jacksonObjectMapper().writeValueAsString(MessagePayload(text = message)
                            )
                        ))
                        .build()
                ).execute()
                .close()
        } catch (e: Error) {
            logger.error("Un-expected error happened while setting Wiremock Scenarios: ${e.localizedMessage}")
        }
    }
}