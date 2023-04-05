package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.TimeEntryController
import fi.metatavu.timebank.api.impl.translate.TimeEntryTranslator
import javax.enterprise.context.RequestScoped
import fi.metatavu.timebank.spec.TimeEntriesApi
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for TimeEntries API
 */
@RequestScoped
class TimeEntriesApi: TimeEntriesApi, AbstractApi() {

    @Inject
    lateinit var timeEntryController: TimeEntryController

    @Inject
    lateinit var timeEntryTranslator: TimeEntryTranslator

    override suspend fun deleteTimeEntry(id: UUID): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")
        if (!isAdmin()) return createUnauthorized(message = "Only admin is allowed to delete timeEntries!")

        timeEntryController.deleteEntry(id = id)

        return createNoContent()
    }

    override suspend fun listTimeEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")

        val entries = timeEntryController.getEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = vacation
        )

        return createOk(
            entity = timeEntryTranslator.translate(entries)
        )
    }
}