package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.PersonsController
import fi.metatavu.timebank.api.impl.translate.PersonsTranslator
import fi.metatavu.timebank.model.Person
import fi.metatavu.timebank.model.Timespan
import fi.metatavu.timebank.spec.PersonsApi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import java.time.LocalDate

/**
 * API implementation for Persons API
 */
@RequestScoped
@OptIn(ExperimentalCoroutinesApi::class)
class PersonsApi: PersonsApi, AbstractApi() {

    @Inject
    lateinit var personsController: PersonsController

    @Inject
    lateinit var personsTranslator: PersonsTranslator

    @Inject
    lateinit var vertx: Vertx

    override fun listPersonTotalTime(personId: Int, timespan: Timespan?, before: LocalDate?, after: LocalDate?): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("Invalid token!")

        val entries = personsController.makePersonTotal(
            personId = personId,
            timespan = timespan ?: Timespan.ALL_TIME,
            before = before,
            after = after
        ) ?: return@async createNotFound("Cannot calculate totals for given person")

        return@async createOk(entity = entries)
    }.asUni()

    override fun listPersons(active: Boolean?): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("Invalid token!")

        return@async try {
            val persons = personsController.listPersons(active = active) ?: return@async createNotFound("No persons found!")

            val translatedPersons = personsTranslator.translate(entities = persons)

            createOk(entity = translatedPersons)
        } catch (e: Error) {
            createBadRequest(e.localizedMessage)
        }
    }.asUni()

    override fun updatePerson(personId: Int, person: Person): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("Invalid token!")
        if (!isAdmin()) return@async createUnauthorized("Only admin is allowed to perform this action!")

        return@async try {
            createOk(entity = personsController.updatePerson(person))
        } catch (e: Error) {
            createInternalServerError(e.localizedMessage)
        }
    }.asUni()
}