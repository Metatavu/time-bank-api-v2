package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.SlackController
import fi.metatavu.timebank.spec.SlackApi
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

@RequestScoped
class SlackApi: SlackApi, AbstractApi(){

    @Inject
    lateinit var slackController: SlackController

    override suspend fun messageManagers(message: String): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            return createOk(entity = slackController.messageManagers(message))
        } catch (e: Exception) {
            createUnauthorized(e.localizedMessage)
        }
    }
}