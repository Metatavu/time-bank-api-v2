package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationController
import fi.metatavu.timebank.api.impl.translate.VacationRequestTranslator
import fi.metatavu.timebank.model.VacationRequest
import javax.enterprise.context.RequestScoped
import fi.metatavu.timebank.spec.VacationsApi
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for Vacations API
 */
@RequestScoped
class VacationsApi: VacationsApi, AbstractApi() {

    @Inject
    lateinit var vacationController: VacationController

    @Inject
    lateinit var vacationRequestTranslator: VacationRequestTranslator

    override suspend fun deleteVacationRequest(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        vacationController.deleteRequest(id)

        return createNoContent()
    }

    override suspend fun createVacationRequest(vacationRequest: VacationRequest): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        vacationController.createVacationRequest(vacationRequest)

        return createNoContent()
    }

    override suspend fun listVacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val vacationRequests = vacationController.getVacationRequests(
            personId = personId,
            before = before,
            after = after
        )

        return createOk(
            entity = vacationRequestTranslator.translate(vacationRequests)
        )
    }
}