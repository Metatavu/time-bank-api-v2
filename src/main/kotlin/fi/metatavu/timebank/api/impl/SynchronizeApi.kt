package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.SynchronizeController
import fi.metatavu.timebank.spec.SynchronizeApi
import java.time.LocalDate
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response

/**
 * API implementation for Synchronize API
 */
@RequestScoped
class SynchronizeApi: SynchronizeApi, AbstractApi() {

    @Inject
    lateinit var synchronizeController: SynchronizeController
    override suspend fun synchronizeTimeEntries(syncDeleted: Boolean, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")

        return try {
            synchronizeController.synchronize(
                after = after,
                syncDeletedEntries = syncDeleted
            )

            createNoContent()
        } catch (e: Error) {
            createBadRequest(message = e.localizedMessage)
        }
    }
}