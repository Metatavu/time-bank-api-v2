package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationRequestController
import fi.metatavu.timebank.api.controllers.VacationRequestStatusController
import fi.metatavu.timebank.api.impl.translate.VacationRequestStatusTranslator
import fi.metatavu.timebank.model.VacationRequestStatus
import fi.metatavu.timebank.spec.VacationRequestStatusApi
import javax.enterprise.context.RequestScoped
import java.util.*
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for VacationRequestStatus API
 */
@RequestScoped
class VacationRequestStatusApi: VacationRequestStatusApi, AbstractApi() {

    @Inject
    lateinit var vacationRequestController: VacationRequestController

    @Inject
    lateinit var vacationRequestStatusController: VacationRequestStatusController

    @Inject
    lateinit var vacationRequestStatusTranslator: VacationRequestStatusTranslator

    override suspend fun createVacationRequestStatus(id: UUID, vacationRequestStatus: VacationRequestStatus): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")

        if (!isAdmin()) return createForbidden("Permission missing")

        val foundVacationRequest = vacationRequestController.findVacationRequest(id) ?: return createNotFound("Vacation request not found")
        val newStatus = vacationRequestStatusController.createVacationRequestStatus(foundVacationRequest, vacationRequestStatus, userId)

        return createCreated(entity = vacationRequestStatusTranslator.translate(newStatus))
    }

    override suspend fun listVacationRequestStatuses(id: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val statuses = vacationRequestStatusController.listVacationRequestStatus(vacationRequestId = id)

        return createOk(entity = vacationRequestStatusTranslator.translate(statuses))
    }

    override suspend fun findVacationRequestStatus(id: UUID, statusId: UUID): Response {
        loggedUserId ?: return createUnauthorized("Invalid token!")

        val foundStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return createNotFound("Vacation request status not found")

        return createOk(vacationRequestStatusTranslator.translate(foundStatus))
    }

    override suspend fun updateVacationRequestStatus(id: UUID, statusId: UUID, vacationRequestStatus: VacationRequestStatus): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")
        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return createNotFound("Vacation request status not found")

        if (existingStatus.createdBy != userId || !isAdmin()) return createForbidden("You can only edit your own statuses")

        val updatedStatus = vacationRequestStatusController.updateVacationRequestStatus(
            existingStatus = existingStatus,
            vacationRequestStatus = vacationRequestStatus,
            updaterId = userId
        )

        return createOk(entity = vacationRequestStatusTranslator.translate(updatedStatus))

    }

    override suspend fun deleteVacationRequestStatus(id: UUID, statusId: UUID): Response {
        val userId = loggedUserId ?: return createUnauthorized("Invalid token!")
        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return createNotFound("Vacation request status not found")

        if (existingStatus.createdBy != userId) return createForbidden("You can only delete your own statuses")

        vacationRequestStatusController.deleteVacationRequestStatus(existingStatus)

        return createNoContent()
    }
}