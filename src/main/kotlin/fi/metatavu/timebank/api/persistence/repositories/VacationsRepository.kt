package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.time.LocalDate
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

/**
 * Manages TimeEntry JPA entity
 */
@ApplicationScoped
class VacationsRepository: PanacheRepositoryBase<VacationRequest, UUID> {

    /**
     * Lists TimeEntries based on given parameters
     *
     * @param personId persons id in Forecast
     * @param before LocalDate to retrieve entries before given date
     * @param after LocalDate to retrieve entries after given date
     * @return List of VacationRequests
     */
    suspend fun getVacationRequest(personId: Int?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        val stringBuilder = StringBuilder()
        val parameters = Parameters()

        if (personId != null) {
            stringBuilder.append("person = :personId")
            parameters.and("personId", personId)
        }

        if (before != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date <= :before" else "date <= :before")
            parameters.and("before", before)
        }

        if (after != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date >= :after" else "date >= :after")
            parameters.and("after", after)
        }
        stringBuilder.append(" order by date DESC")

        return find(stringBuilder.toString(), parameters).list<VacationRequest>().awaitSuspending()
        }



    /**
     * Persists new VacationRequest
     * Replaces already stored request if request is updated
     *
     * @param request VacationRequest
     * @return true for persisted false for not persisted
     */
    suspend fun persistEntry(request: VacationRequest): Boolean {
        Panache.withTransaction { persist(request) }.awaitSuspending()
        return true
    }


    /**
     * Deletes persisted VacationRequest based on entryId
     *
     * @param id id of time registration
     */
    suspend fun deleteEntry(id: UUID) {
        Panache.withTransaction { deleteById(id) }.awaitSuspending()
    }
}