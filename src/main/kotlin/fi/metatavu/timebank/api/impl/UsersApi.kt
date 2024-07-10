package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.UsersController
import fi.metatavu.timebank.spec.UsersApi
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

@RequestScoped
class UsersApi: UsersApi, AbstractApi() {

    @Inject
    lateinit var usersController: UsersController

    override suspend fun findUser(userId: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")
        val user = usersController.findUser(userId)

        return if (user != null) {
            createOk(entity = user)
        } else {
            createNotFound("Person with id $userId not found!")
        }
    }

    override suspend fun listUsers(): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            val persons = usersController.listUsers() ?: return createNotFound("No persons found!")
            createOk(entity = persons)
        } catch (e: Exception){
            createBadRequest(e.localizedMessage)
        }
    }
}