package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.impl.translate.UserTranslator
import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.model.User
import org.keycloak.representations.idm.UserRepresentation
import java.util.UUID
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for User objects
 */
@ApplicationScoped
class UsersController {

    @Inject
    lateinit var keycloakController: KeycloakController

    @Inject
    lateinit var userTranslator: UserTranslator

    /**
     * Fetches User from keycloak
     *
     * @param userId UUID
     * @return UserRepresentation
     */
    fun findUser(userId: UUID): UserRepresentation? {
        return keycloakController.findUserById(userId)
    }

    /**
     * Lists Users found in Keycloak
     *
     * @return List<User>
     */
    fun listUsers(): List<User>? {
        val users = keycloakController.getUsersResource()
        if (users != null) {
            return userTranslator.translate(keycloakController.getUsersResource()!!)
        }

        return null
    }
}