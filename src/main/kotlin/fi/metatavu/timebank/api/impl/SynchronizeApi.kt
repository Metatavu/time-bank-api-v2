package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.SynchronizeController
import fi.metatavu.timebank.spec.SynchronizeApi
import java.time.LocalDate
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for Synchronize API
 */
@RequestScoped
class SynchronizeApi: SynchronizeApi, AbstractApi() {

    @Inject
    lateinit var synchronizeController: SynchronizeController
    override suspend fun synchronizeDeletedTimeEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): Response {
        synchronizeController.synchronizeDeletions(personId = personId, before = before, after = after, vacation = vacation)
        return createNoContent()
    }

    override suspend fun synchronizeTimeEntries(before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")

        return try {
            synchronizeController.synchronize(after)

            createNoContent()
        } catch (e: Error) {
            createBadRequest(message = e.localizedMessage)
        }
    }
}