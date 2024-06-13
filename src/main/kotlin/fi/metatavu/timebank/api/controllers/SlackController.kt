package fi.metatavu.timebank.api.controllers

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotFoundException

@ApplicationScoped
class SlackController {

    /**
     * Sends a Slack message to managers. Return true/false depending on success
     *
     * @param message String
     * @return Boolean
     */
    suspend fun messageManagers(message: String): Boolean {
        val webhook = System.getenv("SLACK_WEBHOOK_URL") ?: return false
        val payload = """{"text": "$message"}"""

        val url = URL(webhook)
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
                    return false
                }
            }
        }
        val responseCode = httpConn.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            true
        } else {
            false
        }
    }
}