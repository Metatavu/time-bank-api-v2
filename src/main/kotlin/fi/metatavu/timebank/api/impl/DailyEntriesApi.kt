package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.DailyEntryController
import fi.metatavu.timebank.spec.DailyEntriesApi
import io.quarkus.security.UnauthorizedException
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import java.time.LocalDate

/**
 * API implementation for DailyEntries API
 */
@RequestScoped
class DailyEntriesApi: DailyEntriesApi, AbstractApi() {

    @Inject
    lateinit var dailyEntryController: DailyEntryController

    override fun listDailyEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): Uni<Response> {
        return Uni.createFrom().item { loggedUserId }
            .onItem().ifNull().failWith( UnauthorizedException("No logged in user!"))
            .onItem().transform { userId ->
                try {
                    val entries = dailyEntryController.list(
                        personId = personId,
                        before = before,
                        after = after,
                        vacation = vacation
                    ) ?: return@transform createNotFound("No daily entries found!")

                    createOk(entity = entries)
                } catch (e: Error) {
                    createInternalServerError(e.localizedMessage)
                }
            }
    }
}