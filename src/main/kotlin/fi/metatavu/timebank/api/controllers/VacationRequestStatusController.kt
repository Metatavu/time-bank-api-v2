package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import fi.metatavu.timebank.api.persistence.model.VacationRequestStatus
import fi.metatavu.timebank.api.persistence.repositories.VacationsRequestStatusRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for vacationRequest objects
 */
@ApplicationScoped
class VacationRequestStatusController {

    @Inject
    lateinit var vacationsRequestStatusRepository: VacationsRequestStatusRepository

    /**
     * Creates and persists new VacationRequestStatus
     *
     * @param vacationRequestStatus VacationRequestStatus
     * @return List of VacationRequestStatuses
     */
    suspend fun createVacationRequestStatus(vacationRequest: VacationRequest, vacationRequestStatus: fi.metatavu.timebank.model.VacationRequestStatus, creatorsId: UUID): VacationRequestStatus {
        return vacationsRequestStatusRepository.persistSuspending(VacationRequestStatus(
            id = UUID.randomUUID(),
            vacationRequest = vacationRequest,
            status = vacationRequestStatus.status,
            message = vacationRequestStatus.message,
            createdBy = creatorsId,
            createdAt = vacationRequestStatus.createdAt,
            updatedAt = null,
        ))
    }

    /**
     * Lists persisted VacationRequestStatuses
     *
     * @param vacationRequestId VacationRequests id
     * @return List of VacationRequestStatuses
     */
    suspend fun listVacationRequestStatus(vacationRequestId: UUID?): List<VacationRequestStatus> {
        return vacationsRequestStatusRepository.listVacationRequestStatus(
            vacationRequestId = vacationRequestId
        )
    }

    /**
     * Finds vacation request status by id
     *
     * @param statusId id of vacation request status
     * @return persisted VacationRequest
     */
    suspend fun findVacationRequestStatus(statusId: UUID): VacationRequestStatus? {
        return vacationsRequestStatusRepository.findSuspending(statusId)
    }

    /**
     * Updates persisted VacationRequestStatus
     *
     * @param existingStatus already persisted VacationRequestStatus
     * @param vacationRequestStatus updated VacationRequestStatus
     * @return persisted VacationRequest
     */
    suspend fun updateVacationRequestStatus(existingStatus: VacationRequestStatus, vacationRequestStatus: fi.metatavu.timebank.model.VacationRequestStatus, updaterId: UUID): VacationRequestStatus {
        existingStatus.status = vacationRequestStatus.status
        existingStatus.message = vacationRequestStatus.message
        existingStatus.updatedBy = updaterId
        existingStatus.updatedAt = vacationRequestStatus.updatedAt

        return vacationsRequestStatusRepository.persistSuspending(existingStatus)
    }

    /**
     * Deletes given VacationRequestStatus
     *
     * @param vacationRequestStatus vacation request status
     */
    suspend fun deleteVacationRequestStatus(vacationRequestStatus: VacationRequestStatus) {
        vacationsRequestStatusRepository.deleteSuspending(vacationRequestStatus)
    }
}
