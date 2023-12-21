package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.TimeEntryController
import fi.metatavu.timebank.api.impl.translate.TimeEntryTranslator
import fi.metatavu.timebank.spec.TimeEntriesApi
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
import java.util.*

/**
 * API implementation for TimeEntries API
 */
@RequestScoped
@OptIn(ExperimentalCoroutinesApi::class)
class TimeEntriesApi: TimeEntriesApi, AbstractApi() {

    @Inject
    lateinit var timeEntryController: TimeEntryController

    @Inject
    lateinit var timeEntryTranslator: TimeEntryTranslator

    @Inject
    lateinit var vertx: Vertx

    override fun deleteTimeEntry(id: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized(message = "Invalid token!")
        if (!isAdmin()) return@async createUnauthorized(message = "Only admin is allowed to delete timeEntries!")

        timeEntryController.deleteEntry(id = id)

        return@async createNoContent()
    }.asUni()

    override fun listTimeEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized(message = "Invalid token!")

        val entries = timeEntryController.getEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = vacation
        )

        if (entries.isEmpty()) {
            return@async createNotFound()
        }

        return@async createOk(
            entity = timeEntryTranslator.translate(entries)
        )
    }.asUni()
}