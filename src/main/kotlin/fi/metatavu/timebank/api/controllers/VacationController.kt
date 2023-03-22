package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.VacationRequest
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
     *
     */
    suspend fun getVacationRequests(personId: Int?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        return vacationsRepository.getVacationRequest(
            personId = personId,
            before = before,
            after = after
        )
    }

    /**
     *
     */
    suspend fun createRequest(vacationRequest: VacationRequest): Boolean {
        val newVacationRequest = VacationRequest()
        newVacationRequest.id = UUID.randomUUID()
        newVacationRequest.message = vacationRequest.message
        newVacationRequest.person = vacationRequest.person
        newVacationRequest.days = vacationRequest.days
        newVacationRequest.startDate = vacationRequest.startDate
        newVacationRequest.endDate = vacationRequest.endDate
        newVacationRequest.status = vacationRequest.status

        return vacationsRepository.persistEntry(newVacationRequest)
    }

    /**
     * Deletes given persisted VacationRequest
     *
     * @param id id
     */
    suspend fun deleteEntry(id: UUID) {
        vacationsRepository.deleteEntry(id = id)
    }
}
