package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.model.User
import org.keycloak.representations.idm.UserRepresentation
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserTranslator: AbstractTranslator<UserRepresentation, User>() {

    override fun translate(entity: UserRepresentation): User {
        return User(
            id = UUID.fromString(entity.id),
            email = entity.email,
            firstName = entity.firstName ?: "",
            lastName = entity.lastName ?: "",
            isActive = entity.isEnabled
        )
    }
}