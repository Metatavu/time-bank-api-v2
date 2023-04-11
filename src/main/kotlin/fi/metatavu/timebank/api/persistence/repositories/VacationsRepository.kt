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
 * Manages VacationRequest JPA entity
 */
@ApplicationScoped
class VacationsRepository: PanacheRepositoryBase<VacationRequest, UUID> {

    /**
     * Lists VacationRequests based on given parameters
     *
     * @param personId persons id in Forecast
     * @param before LocalDate to retrieve requests before given date
     * @param after LocalDate to retrieve requests after given date
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
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date <= :before" else "createdAt <= :before")
            parameters.and("before", before)
        }

        if (after != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date >= :after" else "createdAt >= :after")
            parameters.and("after", after)
        }
        stringBuilder.append(" order by person DESC")

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
        val existingRequest = find("id", request.id).list<VacationRequest>().awaitSuspending()
        if (existingRequest.isEmpty()) {
            Panache.withTransaction { persist(request) }.awaitSuspending()
            return true
        }

        if (request.updatedAt!! > request.createdAt!!) {
            return if (existingRequest.first() == request) {
                false
            } else {
                deleteRequest(request.id!!)
                Panache.withTransaction { persist(request) }.awaitSuspending()
                true
            }
        }

        return false
    }

    /**
     * Deletes persisted VacationRequest based on requestId
     *
     * @param id id of vacationRequest
     */
    suspend fun deleteRequest(id: UUID) {
        Panache.withTransaction { deleteById(id) }.awaitSuspending()
    }
}