package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.PersonsController
import fi.metatavu.timebank.api.impl.translate.PersonsTranslator
import fi.metatavu.timebank.model.Person
import fi.metatavu.timebank.model.Timespan
import fi.metatavu.timebank.spec.PersonsApi
import java.time.LocalDate
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for Persons API
 */
@RequestScoped
class PersonsApi: PersonsApi, AbstractApi() {

    @Inject
    lateinit var personsController: PersonsController

    @Inject
    lateinit var personsTranslator: PersonsTranslator

    override suspend fun listPersonTotalTime(personId: Int, timespan: Timespan?, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val entries = personsController.makePersonTotal(
            personId = personId,
            timespan = timespan ?: Timespan.ALL_TIME,
            before = before,
            after = after
        )
        
        return createOk(entity = entries)
    }

    override suspend fun listPersons(active: Boolean?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            val persons = personsController.listPersons(active = active) ?: return createNotFound("No persons found!")

            val translatedPersons = personsTranslator.translate(entities = persons)

            createOk(entity = translatedPersons)
        } catch (e: Error) {
            createBadRequest(e.localizedMessage)
        }
    }

    override suspend fun updatePerson(personId: Int, person: Person): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")
        if (!isAdmin()) return createUnauthorized("Only admin is allowed to perform this action!")

        return try {
            createOk(entity = personsController.updatePerson(person))
        } catch (e: Error) {
            createInternalServerError(e.localizedMessage)
        }
    }
}