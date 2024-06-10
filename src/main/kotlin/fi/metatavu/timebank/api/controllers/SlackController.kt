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
        val webhookUrl = ""
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
            throw BadRequestException("Error occurred when sending message on Slack")
        }
    }
}