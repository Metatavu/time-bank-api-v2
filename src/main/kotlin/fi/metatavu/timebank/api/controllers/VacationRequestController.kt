package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import fi.metatavu.timebank.api.persistence.repositories.VacationsRequestsRepository
import java.time.LocalDate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for vacationRequest objects
 */
@ApplicationScoped
class VacationRequestController {

    @Inject
    lateinit var vacationsRequestsRepository: VacationsRequestsRepository

    /**
     * Gets persisted VacationRequests
     *
     * @param personId personId
     * @param before before
     * @param after after
     * @return List of VacationRequests
     */
    suspend fun listVacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        return vacationsRequestsRepository.listVacationRequest(
            personId = personId,
            before = before,
            after = after
        )
    }

    /**
     * Creates and persists new VacationRequest
     *
     * @param vacationRequest VacationRequest
     * @return persisted VacationRequest
     */
    suspend fun createVacationRequest(vacationRequest: fi.metatavu.timebank.model.VacationRequest, creatorsId: UUID): VacationRequest {
        return vacationsRequestsRepository.persistSuspending(
            VacationRequest(
                id = UUID.randomUUID(),
                person = vacationRequest.person,
                startDate = vacationRequest.startDate,
                endDate = vacationRequest.endDate,
                days = vacationRequest.days,
                type = vacationRequest.type,
                message = vacationRequest.message,
                projectManagerStatus = vacationRequest.projectManagerStatus,
                hrManagerStatus = vacationRequest.hrManagerStatus,
                createdAt = vacationRequest.createdAt,
                createdBy = creatorsId,
                updatedAt = vacationRequest.updatedAt,
                lastModifiedBy = creatorsId
            )
        )
    }

    /**
     * Updates persisted VacationRequest
     *
     * @param existingVacationRequest already persisted VacationRequest
     * @param vacationRequest updated VacationRequest
     * @return persisted VacationRequest
     */
    suspend fun updateVacationRequest(existingVacationRequest: VacationRequest, vacationRequest: fi.metatavu.timebank.model.VacationRequest, modifiersId: UUID): VacationRequest {
        existingVacationRequest.startDate = vacationRequest.startDate
        existingVacationRequest.endDate = vacationRequest.endDate
        existingVacationRequest.days = vacationRequest.days
        existingVacationRequest.type = vacationRequest.type
        existingVacationRequest.message = vacationRequest.message
        existingVacationRequest.projectManagerStatus = vacationRequest.projectManagerStatus
        existingVacationRequest.hrManagerStatus = vacationRequest.hrManagerStatus
        existingVacationRequest.updatedAt = vacationRequest.updatedAt
        existingVacationRequest.lastModifiedBy = modifiersId

        return vacationsRequestsRepository.persistSuspending(existingVacationRequest)
    }

    /**
     * Finds vacation request by id
     *
     * @param id id
     * @return persisted VacationRequest
     */
    suspend fun findVacationRequest(id: UUID): VacationRequest {
       return vacationsRequestsRepository.findSuspending(id)
    }

    /**
     * Deletes given VacationRequest
     *
     * @param id id
     */
    suspend fun deleteVacationRequest(id: UUID) {
        vacationsRequestsRepository.deleteSuspending(id)
    }
}
