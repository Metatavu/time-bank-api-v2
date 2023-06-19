package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.VacationRequestStatus
import fi.metatavu.timebank.api.persistence.repositories.VacationsRequestStatusRepository
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for vacationRequestStatus objects
 */
@ApplicationScoped
class VacationRequestStatusController {

    @Inject
    lateinit var vacationsRequestStatusRepository: VacationsRequestStatusRepository

    /**
     * Gets persisted VacationRequestStatuses
     *
     * @param vacationRequestId VacatonRequests id
     * @param personId personId
     * @return List of VacationRequestStatuses
     */
    suspend fun listVacationRequestStatus(vacationRequestId: UUID?, personId: Int?): List<VacationRequestStatus> {
        return vacationsRequestStatusRepository.listVacationRequestStatus(
            vacationRequestId = vacationRequestId?.toString(),
            personId = personId
        )
    }

    /**
     * Finds vacation request status by id
     *
     * @param id id
     * @return persisted VacationRequest
     */
    suspend fun findVacationRequestStatus(id: UUID): VacationRequestStatus {
        return vacationsRequestStatusRepository.findSuspending(id)
    }

    /**
     * Creates and persists new VacationRequestStatus
     *
     * @param vacationRequestStatus VacationRequestStatus
     * @return List of VacationRequestStatuses
     */
    suspend fun createVacationRequestStatus(vacationRequestStatus: fi.metatavu.timebank.model.VacationRequestStatus): VacationRequestStatus {
        return vacationsRequestStatusRepository.persistSuspending(VacationRequestStatus(
            id = UUID.randomUUID(),
            person = vacationRequestStatus.person,
            vacationRequestId = vacationRequestStatus.vacationRequestId,
            status = vacationRequestStatus.status,
            message = vacationRequestStatus.message,
            updatedAt = vacationRequestStatus.updatedAt
        ))
    }

    /**
     * Updates persisted VacationRequestStatus
     *
     * @param existingStatus already persisted VacationRequestStatus
     * @param vacationRequestStatus updated VacationRequestStatus
     * @return persisted VacationRequest
     */
    suspend fun updateVacationRequestStatus(existingStatus: VacationRequestStatus, vacationRequestStatus: fi.metatavu.timebank.model.VacationRequestStatus): VacationRequestStatus {
        existingStatus.status = vacationRequestStatus.status
        existingStatus.message = vacationRequestStatus.message
        existingStatus.updatedAt = vacationRequestStatus.updatedAt

        return vacationsRequestStatusRepository.persistSuspending(existingStatus)
    }

    /**
     * Deletes given VacationRequestStatus
     *
     * @param id id of vacation request status
     */
    suspend fun deleteVacationRequestStatus(id: UUID) {
        vacationsRequestStatusRepository.deleteSuspending(id)
    }
}
