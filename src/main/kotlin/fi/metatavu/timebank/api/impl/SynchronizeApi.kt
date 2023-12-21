package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.SynchronizeController
import fi.metatavu.timebank.spec.SynchronizeApi
import io.quarkus.security.UnauthorizedException
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import java.time.LocalDate

/**
 * API implementation for Synchronize API
 */
@RequestScoped
class SynchronizeApi: SynchronizeApi, AbstractApi() {

    @Inject
    lateinit var synchronizeController: SynchronizeController
    override fun synchronizeTimeEntries(syncDeleted: Boolean, before: LocalDate?, after: LocalDate?): Uni<Response> {
        return Uni.createFrom().item { loggedUserId }
            .onItem().ifNull().failWith { UnauthorizedException("Invalid token!") }
            .onItem().transform { userId ->
                try {
                    synchronizeController.synchronize(after = after, syncDeletedEntries = syncDeleted)
                    createNoContent()
                } catch (e: Error) {
                    createBadRequest(message = e.localizedMessage)
                }
            }
    }
}