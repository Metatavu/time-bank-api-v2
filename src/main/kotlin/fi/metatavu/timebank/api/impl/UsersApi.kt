package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.UsersController
import fi.metatavu.timebank.api.impl.translate.UserTranslator
import fi.metatavu.timebank.model.User
import fi.metatavu.timebank.spec.UsersApi
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for Users API
 */
@RequestScoped
class UsersApi: UsersApi, AbstractApi() {

    @Inject
    lateinit var usersController: UsersController

    @Inject
    lateinit var userTranslator: UserTranslator

    override suspend fun findUser(userId: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")
        val user = usersController.findUser(userId) ?: return createNotFound("Person with id $userId not found!")

        return createOk(entity = userTranslator.translate(user))
    }

    override suspend fun listUsers(): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            val persons = usersController.listUsers()
            createOk(entity = userTranslator.translate(persons))
        } catch (e: Exception){
            createBadRequest(e.localizedMessage)
        }
    }
}