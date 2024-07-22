package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.keycloak.KeycloakController
import org.keycloak.representations.idm.UserRepresentation
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for User objects
 */
@ApplicationScoped
class UsersController {

    @Inject
    lateinit var keycloakController: KeycloakController

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
    fun listUsers(): List<UserRepresentation> {
        return keycloakController.searchUsers()
    }
}