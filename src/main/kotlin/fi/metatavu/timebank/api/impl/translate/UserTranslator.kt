package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.model.Person
import fi.metatavu.timebank.model.User
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.UserRepresentation
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class UserTranslator: AbstractTranslator<UserRepresentation, User>() {
    @Inject
    lateinit var keycloakController: KeycloakController

    override fun translate(entity: UserRepresentation): User {
        val keycloakUser = keycloakController.findUserByEmail(entity.email)

        return User(
            id = keycloakUser?.let { UUID.fromString(keycloakUser.id) }!!,
            email = entity.email,
            firstName = entity.firstName,
            lastName = entity.lastName,
            isActive = true
        )
    }

    fun translate(entities: UsersResource): List<User> {
        val usersRepresentations: List<UserRepresentation> = entities.list()

        return usersRepresentations.map { userRep ->
            val keycloakUser = keycloakController.findUserByEmail(userRep.email)
            User(
                id = keycloakUser?.let { UUID.fromString(keycloakUser.id) }!!,
                isActive = true,
                firstName = userRep.firstName,
                lastName = userRep.lastName,
                email = userRep.email
            )
        }
    }
}