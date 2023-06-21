package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationRequestController
import fi.metatavu.timebank.api.impl.translate.VacationRequestTranslator
import fi.metatavu.timebank.model.VacationRequest
import javax.enterprise.context.RequestScoped
import fi.metatavu.timebank.spec.VacationRequestsApi
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for VacationRequests API
 */
@RequestScoped
class VacationRequestsApi: VacationRequestsApi, AbstractApi() {

    @Inject
    lateinit var vacationRequestController: VacationRequestController

    @Inject
    lateinit var vacationRequestTranslator: VacationRequestTranslator

    override suspend fun createVacationRequest(vacationRequest: VacationRequest): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")

        val newVacationRequest = vacationRequestController.createVacationRequest(
            vacationRequest = vacationRequest,
            creatorsId = userId
        )

        return createCreated(entity = vacationRequestTranslator.translate(newVacationRequest))
    }

    override suspend fun listVacationRequests(personId: UUID?, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val vacationRequests = vacationRequestController.listVacationRequests(
            personId = personId,
            before = before,
            after = after
        )

        return createOk(entity = vacationRequestTranslator.translate(vacationRequests))
    }

    override suspend fun findVacationRequest(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return createOk(vacationRequestController.findVacationRequest(id))
    }

    override suspend fun updateVacationRequest(id: UUID, vacationRequest: VacationRequest): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")
        val existingVacationRequest = vacationRequestController.findVacationRequest(id) ?: return createNotFound("Vacation request not found")

        if ( existingVacationRequest.person != userId) return createForbidden("You can only edit your own requests")

        val updatedVacationRequest = vacationRequestController.updateVacationRequest(
            existingVacationRequest = existingVacationRequest,
            vacationRequest = vacationRequest,
        )

        return createOk(entity = vacationRequestTranslator.translate(updatedVacationRequest))
    }

    override suspend fun deleteVacationRequest(id: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")
        val existingVacationRequest = vacationRequestController.findVacationRequest(id) ?: return createNotFound("Vacation request not found")

        if ( existingVacationRequest.person != userId) return createForbidden("You can only delete your own requests")

        vacationRequestController.deleteVacationRequest(id)

        return createNoContent()
    }
}