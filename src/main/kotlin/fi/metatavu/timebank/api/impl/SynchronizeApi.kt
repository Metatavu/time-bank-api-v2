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

    override suspend fun synchronizeDeletedTimeEntries(before: LocalDate?, after: LocalDate?): Response {
        synchronizeController.startSynchronization(
            after = after,
            syncDeletedEntries = true
        )

        return createNoContent()
    }

    override suspend fun synchronizeTimeEntries(before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")

        return try {
            synchronizeController.startSynchronization(
                after = after,
                syncEntries = true
            )

            createNoContent()
        } catch (e: Error) {
            createBadRequest(message = e.localizedMessage)
        }
    }
}