package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationController
import fi.metatavu.timebank.model.VacationRequest
import javax.enterprise.context.RequestScoped
import fi.metatavu.timebank.spec.VacationsApi
import java.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for TimeEntries API
 */
@RequestScoped
class VacationsApi: VacationsApi, AbstractApi() {

    @Inject
    lateinit var vacationController: VacationController

    override suspend fun newVacationRequest(vacationRequest: VacationRequest): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")
        return createNoContent()
    }

    override suspend fun vacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized(message = "Invalid token!")

        val vacationRequests = vacationController.getVacationRequests(
            personId = personId,
            before = before,
            after = after
        )

        if (vacationRequests.isEmpty()) {
            return createNotFound()
        }

        return createOk(

        )
    }



}