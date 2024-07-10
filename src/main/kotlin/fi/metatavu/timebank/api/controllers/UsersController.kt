package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.impl.translate.UserTranslator
import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.model.User
import java.util.UUID
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class UsersController {

    @Inject
    lateinit var keycloakController: KeycloakController

    @Inject
    lateinit var userTranslator: UserTranslator

    fun findUser(userId: UUID): User? {
        val user = keycloakController.findUserById(userId)
        if (user != null) {
            return userTranslator.translate(user)
        }

        return null
    }

    fun listUsers(): List<User>? {
        val users = keycloakController.getUsersResource()
        if (users != null) {
            return userTranslator.translate(keycloakController.getUsersResource()!!)
        }

        return null
    }
}