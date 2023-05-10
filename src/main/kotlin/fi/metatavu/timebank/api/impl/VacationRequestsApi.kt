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

    override suspend fun deleteVacationRequest(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val existingVacationRequest = vacationRequestController.findVacationRequest(id)

        if(isAdmin() || loggedUserId == existingVacationRequest.createdBy) {

            vacationRequestController.deleteVacationRequest(id)

            return createNoContent()
        }
        return createUnauthorized("Permission missing")
    }

    override suspend fun findVacationRequest(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            return createOk(vacationRequestController.findVacationRequest(id))
        } catch (e: Error) {
            createInternalServerError(e.localizedMessage)
        }
    }

    override suspend fun updateVacationRequest(id: UUID, vacationRequest: VacationRequest): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")
        if(isAdmin() || loggedUserId == vacationRequest.createdBy) {
            val existingVacationRequest = vacationRequestController.findVacationRequest(id)

            val updatedVacationRequest = vacationRequestController.updateVacationRequest(
                existingVacationRequest = existingVacationRequest,
                vacationRequest = vacationRequest,
                modifiersId = loggedUserId!!
            )

            return try {
                return createOk(entity = vacationRequestTranslator.translate(updatedVacationRequest))
            } catch (e: Error) {
                createInternalServerError(e.localizedMessage)
            }
        }
        return createUnauthorized("Permission missing")
    }

    override suspend fun createVacationRequest(vacationRequest: VacationRequest): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val newVacationRequest = vacationRequestController.createVacationRequest(
            vacationRequest = vacationRequest,
            creatorsId = loggedUserId!!
        )

        return try {
            return createCreated(entity = vacationRequestTranslator.translate(newVacationRequest))
        } catch (e: Error) {
            createInternalServerError(e.localizedMessage)
        }
    }


    override suspend fun listVacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val vacationRequests = vacationRequestController.listVacationRequests(
            personId = personId,
            before = before,
            after = after
        )

        return createOk(entity = vacationRequestTranslator.translate(vacationRequests))
    }
}