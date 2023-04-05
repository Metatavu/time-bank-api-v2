package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.model.VacationRequest
import fi.metatavu.timebank.api.persistence.repositories.VacationsRepository
import java.time.LocalDate
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for timeEntry objects
 */
@ApplicationScoped
class VacationController {

    @Inject
    lateinit var vacationsRepository: VacationsRepository

    /**
     * Gets persisted VacationRequests
     *
     * @param personId personId
     * @param before before
     * @param after after
     * @return List of VacationRequests
     */
    suspend fun getVacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): List<fi.metatavu.timebank.api.persistence.model.VacationRequest> {
        return vacationsRepository.getVacationRequest(
            personId = personId,
            before = before,
            after = after
        )
    }

    /**
     * Creates and persists new VacationRequest
     *
     * @param vacationRequest VacationRequest
     * @return boolean whether operation was successful
     */
    suspend fun createVacationRequest(vacationRequest: VacationRequest): Boolean {
        val newVacationRequest = fi.metatavu.timebank.api.persistence.model.VacationRequest()
        newVacationRequest.id = vacationRequest.id ?: UUID.randomUUID()
        newVacationRequest.person = vacationRequest.person
        newVacationRequest.startDate = vacationRequest.startDate
        newVacationRequest.endDate = vacationRequest.endDate
        newVacationRequest.days = vacationRequest.days
        newVacationRequest.type = vacationRequest.type.value
        newVacationRequest.message = vacationRequest.message
        newVacationRequest.projectManagerStatus = vacationRequest.projectManagerStatus.value
        newVacationRequest.hrManagerStatus = vacationRequest.hrManagerStatus.value
        newVacationRequest.createdAt = vacationRequest.createdAt
        newVacationRequest.updatedAt = vacationRequest.updatedAt

        return vacationsRepository.persistEntry(newVacationRequest)
    }

    /**
     * Deletes given persisted VacationRequest
     *
     * @param id id
     */
    suspend fun deleteRequest(id: UUID) {
        vacationsRepository.deleteRequest(id = id)
    }
}
