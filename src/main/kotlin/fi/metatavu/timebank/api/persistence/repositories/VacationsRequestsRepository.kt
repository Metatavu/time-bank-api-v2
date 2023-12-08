package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import io.quarkus.panache.common.Parameters
import java.time.LocalDate
import java.util.UUID
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages VacationRequest JPA entity
 */
@ApplicationScoped
class VacationsRequestsRepository: AbstractRepository<VacationRequest, UUID>() {

    /**
     * Lists VacationRequests based on given parameters
     *
     * @param personId persons id
     * @param before LocalDate to retrieve requests before given date
     * @param after LocalDate to retrieve requests after given date
     * @return List of VacationRequests
     */
    suspend fun listVacationRequest(personId: UUID?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        val stringBuilder = StringBuilder()
        val parameters = Parameters()

        if (personId != null) {
            stringBuilder.append("personId = :personId")
            parameters.and("personId", personId)
        }

        if (before != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and startDate <= :before" else "startDate <= :before")
            parameters.and("before", before)
        }

        if (after != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and endDate >= :after" else "endDate >= :after")
            parameters.and("after", after)
        }

        stringBuilder.append(" order by startDate DESC")

        return listWithParameters(stringBuilder.toString(), parameters)
    }
}