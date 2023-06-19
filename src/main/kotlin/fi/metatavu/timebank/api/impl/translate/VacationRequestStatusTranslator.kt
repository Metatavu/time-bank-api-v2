package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.persistence.model.VacationRequestStatus
import javax.enterprise.context.ApplicationScoped

/**
 * Translates VacationRequestStatus objects
 */
@ApplicationScoped
class VacationRequestStatusTranslator: AbstractTranslator<VacationRequestStatus, fi.metatavu.timebank.model.VacationRequestStatus>() {

    override fun translate(entity: VacationRequestStatus): fi.metatavu.timebank.model.VacationRequestStatus {
        return fi.metatavu.timebank.model.VacationRequestStatus(
            id = entity.id,
            vacationRequestId = entity.vacationRequestId!!,
            person = entity.person!!,
            status = entity.status!!,
            message = entity.message,
            updatedAt = entity.updatedAt!!
        )
    }

    override fun translate(entities: List<VacationRequestStatus>): List<fi.metatavu.timebank.model.VacationRequestStatus> {
        return entities.map(this::translate)
    }
}