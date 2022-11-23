package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.persistence.model.TimeEntry
import javax.enterprise.context.ApplicationScoped

/**
 * Translates TimeEntry objects
 */
@ApplicationScoped
class TimeEntryTranslator: AbstractTranslator<TimeEntry, fi.metatavu.timebank.model.TimeEntry>() {

    override fun translate(entity: TimeEntry): fi.metatavu.timebank.model.TimeEntry {
        return fi.metatavu.timebank.model.TimeEntry(
            id = entity.id,
            forecastId = entity.forecastId,
            person = entity.person!!,
            internalTime =  entity.internalTime!!,
            miscTime = entity.miscTime!!,
            billableProjectTime = entity.billableProjectTime!!,
            nonBillableProjectTime = entity.nonBillableProjectTime!!,
            date = entity.date!!,
            createdAt = entity.createdAt!!,
            updatedAt = entity.updatedAt!!,
            isVacation = entity.isVacation!!
        )
    }

    override fun translate(entities: List<TimeEntry>): List<fi.metatavu.timebank.model.TimeEntry> {
        return entities.map(this::translate)
    }
}