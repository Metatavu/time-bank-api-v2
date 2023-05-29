package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.model.Person
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translates ForecastPerson object to Person object
 */
@ApplicationScoped
class PersonsTranslator: AbstractTranslator<ForecastPerson, Person>() {

    @Inject
    lateinit var keycloakController: KeycloakController


    override fun translate(entity: ForecastPerson): Person {
        val keycloakUser = keycloakController.findUserByEmail(entity.email)
        val minimumBillableRate = if (keycloakUser == null) 75 else keycloakController.getUsersMinimumBillableRate(keycloakUser)

        return Person(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            monday = entity.monday,
            tuesday = entity.tuesday,
            wednesday = entity.wednesday,
            thursday = entity.thursday,
            friday = entity.friday,
            saturday = entity.saturday,
            sunday = entity.sunday,
            active = entity.active,
            startDate = entity.startDate,
            language = entity.language,
            unspentVacations = entity.unspentVacations,
            spentVacations = entity.spentVacations,
            minimumBillableRate = minimumBillableRate,
            keycloakId = if (keycloakUser != null) {UUID.fromString(keycloakUser.id)} else null
        )
    }

    override fun translate(entities: List<ForecastPerson>): List<Person> {
        return entities.map(this::translate)
    }
}