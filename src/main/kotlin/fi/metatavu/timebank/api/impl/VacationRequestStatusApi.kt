package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationRequestStatusController
import fi.metatavu.timebank.api.impl.translate.VacationRequestStatusTranslator
import fi.metatavu.timebank.model.VacationRequestStatus
import javax.enterprise.context.RequestScoped
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response
import fi.metatavu.timebank.spec.VacationRequestStatusApi

/**
 * API implementation for VacationRequestStatus API
 */
@RequestScoped
class VacationRequestStatusApi: VacationRequestStatusApi, AbstractApi() {

    @Inject
    lateinit var vacationRequestStatusController: VacationRequestStatusController

    @Inject
    lateinit var vacationRequestStatusTranslator: VacationRequestStatusTranslator

    override suspend fun listVacationRequestStatuses(vacationRequestId: UUID?, personId: Int?): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val statuses = vacationRequestStatusController.listVacationRequestStatus(vacationRequestId = vacationRequestId, personId = personId)

        return createOk(entity = vacationRequestStatusTranslator.translate(statuses))
    }

    override suspend fun updateVacationRequestStatus(id: UUID, vacationRequestStatus: VacationRequestStatus): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(id)

        if (vacationRequestStatus.person == existingStatus.person) {
            val updatedStatus = vacationRequestStatusController.updateVacationRequestStatus(
                existingStatus = existingStatus,
                vacationRequestStatus = vacationRequestStatus,
            )

            return try {
                return createOk(entity = vacationRequestStatusTranslator.translate(updatedStatus))
            } catch (e: Error) {
                createInternalServerError(e.localizedMessage)
            }
        }
        return createUnauthorized("Permission missing")
    }

    override suspend fun createVacationRequestStatus(vacationRequestStatus: VacationRequestStatus): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        if (isAdmin()) {
            val newStatus = vacationRequestStatusController.createVacationRequestStatus(vacationRequestStatus)

            return try {
                createCreated(entity = vacationRequestStatusTranslator.translate(newStatus))
            } catch (e: Error) {
                createInternalServerError(e.localizedMessage)
            }
        }
        return createUnauthorized("Permission missing")
    }

    override suspend fun deleteVacationRequestStatus(id: UUID, personId: Int): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(id)

        if (personId == existingStatus.person) {

            vacationRequestStatusController.deleteVacationRequestStatus(id)

            return createNoContent()
        }
        return createUnauthorized("Permission missing")
    }

    override suspend fun findVacationRequestStatus(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        return try {
            return createOk(vacationRequestStatusController.findVacationRequestStatus(id))
        } catch (e: Error) {
            createInternalServerError(e.localizedMessage)
        }
    }
}