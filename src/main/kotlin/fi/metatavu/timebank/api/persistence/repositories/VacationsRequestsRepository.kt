package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import java.time.LocalDate
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

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
    suspend fun listVacationRequest(personId: Int?, before: LocalDate?, after: LocalDate?): List<VacationRequest> {
        val strings = mutableMapOf<String, String>()
        val params = mutableMapOf<String, Any>()
        val order = " order by startDate DESC"

        if (personId != null) {
            strings["personId"] = "person = :personId"
            params["personId"] = personId
        }

        if (before != null) {
            strings["before"] = if (strings.isNotEmpty()) " and endDate <= :before" else "endDate <= :before"
            params["before"] = before
        }

        if (after != null) {
            strings["after"] = if (strings.isNotEmpty()) " and startDate >= :after" else "startDate >= :after"
            params["after"] = after
        }

        return queryBuilder(strings, params, order)
    }
}