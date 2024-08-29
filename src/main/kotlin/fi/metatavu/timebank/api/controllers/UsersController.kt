package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.api.severa.SeveraService
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.Logger
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

    @Inject
    lateinit var severaService: SeveraService

    @Inject
    lateinit var logger: Logger

    /**
     * Fetches User from keycloak. Updates severaGuid -attribute, firstName, and lastName for found user in keycloak.
     *
     * @param userId UUID
     * @return UserRepresentation
     */
    fun findUser(userId: UUID): UserRepresentation? {
        val user = keycloakController.findUserById(userId)

        if (user != null){
            val severaGuid = user.attributes["severa-user-id"]?.firstOrNull()
                ?: severaService.getUsers().find { it.email == user.email }?.guid
            if (severaGuid != null){
                val severaUser = severaService.findUser(severaGuid)
                keycloakController.updateUser(
                    user.apply {
                        firstName = severaUser.firstName
                        lastName = severaUser.lastName
                    },
                    severaGuid
                )
            }
        }

        return user
    }

    /**
     * Lists Users found in Keycloak
     *
     * @return List of UserRepresentation
     */
    fun listUsers(): List<UserRepresentation> {
        return keycloakController.searchUsers()
    }
}