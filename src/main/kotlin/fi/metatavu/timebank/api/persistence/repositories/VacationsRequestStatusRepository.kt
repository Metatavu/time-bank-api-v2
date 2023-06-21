package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.VacationRequestStatus
import io.quarkus.panache.common.Parameters
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

/**
 * Manages VacationRequestStatus JPA entity
 */
@ApplicationScoped
class VacationsRequestStatusRepository: AbstractRepository<VacationRequestStatus, UUID>() {

    /**
     * Lists VacationRequestStatuses based on given parameters
     *
     * @param vacationRequestId id of the VacationRequest
     * @return List of VacationRequests
     */
    suspend fun listVacationRequestStatus(vacationRequestId: UUID?): List<VacationRequestStatus> {
        val stringBuilder = StringBuilder()
        val parameters = Parameters()

        if (vacationRequestId != null) {
            stringBuilder.append("vacationRequestId = :vacationRequestId")
            parameters.and("vacationRequestId", vacationRequestId)
        }

        stringBuilder.append(" order by vacationRequestId DESC")

        return listWithParameters(stringBuilder.toString(), parameters)
    }
}

