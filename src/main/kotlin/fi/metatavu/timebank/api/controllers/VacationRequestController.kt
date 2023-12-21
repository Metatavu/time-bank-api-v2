package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import fi.metatavu.timebank.api.persistence.repositories.VacationsRequestsRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.time.LocalDate
import java.util.*

/**
 * Controller for vacationRequest objects
 */
@ApplicationScoped
class VacationRequestController {

    @Inject
    lateinit var vacationsRequestsRepository: VacationsRequestsRepository

    @Inject
    lateinit var vacationRequestStatusController: VacationRequestStatusController

    /**
     * Creates and persists new VacationRequest
     *
     * @param vacationRequest VacationRequest
     * @return persisted VacationRequest
     */
    fun createVacationRequest(vacationRequest: fi.metatavu.timebank.model.VacationRequest, creatorsId: UUID): VacationRequest {
        return vacationsRequestsRepository.persistSuspending(
            VacationRequest(
                id = UUID.randomUUID(),
                personId = creatorsId,
                startDate = vacationRequest.startDate,
                endDate = vacationRequest.endDate,
                days = vacationRequest.days,
                type = vacationRequest.type,
                message = vacationRequest.message,
                createdAt = vacationRequest.createdAt,
                updatedAt = vacationRequest.updatedAt,
            )
        )
    }

    /**
     * Lists persisted VacationRequests
     *
     * @param personId personId
     * @param before before date
     * @param after after date
     * @return List of VacationRequests
     */
    fun listVacationRequests(personId: UUID?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        return vacationsRequestsRepository.listVacationRequest(
            personId = personId,
            before = before,
            after = after
        )
    }

    /**
     * Finds vacation request by id
     *
     * @param id id
     * @return persisted VacationRequest
     */
    fun findVacationRequest(id: UUID): VacationRequest? {
        return vacationsRequestsRepository.findSuspending(id)
    }

    /**
     * Updates persisted VacationRequest
     *
     * @param existingVacationRequest already persisted VacationRequest
     * @param vacationRequest updated VacationRequest
     * @return persisted VacationRequest
     */
    suspend fun updateVacationRequest(existingVacationRequest: VacationRequest, vacationRequest: fi.metatavu.timebank.model.VacationRequest): VacationRequest {
        existingVacationRequest.startDate = vacationRequest.startDate
        existingVacationRequest.endDate = vacationRequest.endDate
        existingVacationRequest.days = vacationRequest.days
        existingVacationRequest.type = vacationRequest.type
        existingVacationRequest.message = vacationRequest.message
        existingVacationRequest.updatedAt = vacationRequest.updatedAt

        return vacationsRequestsRepository.persistSuspending(existingVacationRequest)
    }

    /**
     * Deletes given VacationRequest
     *
     * @param id id
     */
    suspend fun deleteVacationRequest(id: UUID) {
        vacationRequestStatusController.listVacationRequestStatus(id).forEach {
            vacationRequestStatusController.deleteVacationRequestStatus(it)
        }
        vacationsRequestsRepository.deleteByIdSuspending(id)
    }
}
