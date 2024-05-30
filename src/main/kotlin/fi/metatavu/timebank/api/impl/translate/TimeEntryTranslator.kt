package fi.metatavu.timebank.api.impl.translate

import fi.metatavu.timebank.api.persistence.model.TimeEntry
import javax.enterprise.context.ApplicationScoped

/**
 * Translates TimeEntry objects
 */
@ApplicationScoped
class TimeEntryTranslator: AbstractTranslator<TimeEntry, fi.metatavu.timebank.model.TimeEntry>() {

    override suspend fun translate(entity: TimeEntry): fi.metatavu.timebank.model.TimeEntry {
        return fi.metatavu.timebank.model.TimeEntry(
            id = entity.id,
            forecastId = entity.forecastId,
            person = entity.person!!,
            internalTime =  entity.internalTime!!,
            billableProjectTime = entity.billableProjectTime!!,
            nonBillableProjectTime = entity.nonBillableProjectTime!!,
            date = entity.date!!,
            createdAt = entity.createdAt!!,
            updatedAt = entity.updatedAt!!,
            isVacation = entity.isVacation!!
        )
    }
}