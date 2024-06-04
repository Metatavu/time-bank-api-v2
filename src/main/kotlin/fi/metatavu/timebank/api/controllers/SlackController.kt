package fi.metatavu.timebank.api.controllers

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException

@ApplicationScoped
class SlackController {

    /**
     * Sends a Slack message to managers
     *
     * @param message String
     * @return String
     */
    suspend fun messageManagers(message: String): String{
        /*  THIS IS A DUMMY LINK */
        val webhookUrl = "https://hooks.slack.com/services/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"
        val payload = """{"text": "$message"}"""

        val url = URL(webhookUrl)
        val httpConn = url.openConnection() as HttpURLConnection

        httpConn.doOutput = true
        httpConn.requestMethod = "POST"
        httpConn.setRequestProperty("Content-Type", "application/json")
        httpConn.setRequestProperty("Accept", "application/json")

        httpConn.outputStream.use { outputStream ->
            OutputStreamWriter(outputStream).use { writer ->
                try {
                    writer.write(payload)
                    writer.flush()
                } catch (e: Exception) {
                    return e.stackTraceToString()
                }
            }
        }


        val responseCode = httpConn.responseCode
        val responseMessage = httpConn.responseMessage

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            payload
        } else {
            throw BadRequestException("Error occurred")
        }
    }
}