package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.persistence.model.VacationRequest
import javax.enterprise.context.ApplicationScoped

/**
 * Translates VacationRequest objects
 */
@ApplicationScoped
class VacationRequestTranslator: AbstractTranslator<VacationRequest, fi.metatavu.timebank.model.VacationRequest>() {

    override fun translate(entity: VacationRequest): fi.metatavu.timebank.model.VacationRequest {
        return fi.metatavu.timebank.model.VacationRequest(
            id = entity.id,
            personId = entity.personId!!,
            startDate = entity.startDate!!,
            endDate = entity.endDate!!,
            days = entity.days!!,
            message = entity.message!!,
            type = entity.type!!,
            createdAt = entity.createdAt!!,
            updatedAt = entity.updatedAt!!,
        )
    }

    override fun translate(entities: List<VacationRequest>): List<fi.metatavu.timebank.model.VacationRequest> {
        return entities.map(this::translate)
    }
}