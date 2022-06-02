package fi.metatavu.timebank.api.forecast.translate

import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntry
import fi.metatavu.timebank.api.persistence.model.TimeEntry
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * Translates ForecastTimeEntry object to persistable TimeEntry object
 */
@ApplicationScoped
class ForecastTimeEntryTranslator {

    fun translate(entity: ForecastTimeEntry): TimeEntry {
        val translatedTimeEntry = TimeEntry()
        translatedTimeEntry.entryId = UUID.randomUUID()
        translatedTimeEntry.forecastId = entity.id
        translatedTimeEntry.person = entity.person
        translatedTimeEntry.internalTime = if (entity.non_project_time != null) entity.time_registered else 0
        translatedTimeEntry.projectTime = if (entity.non_project_time != null) 0 else entity.time_registered
        translatedTimeEntry.date = LocalDate.parse(entity.date)
        translatedTimeEntry.createdAt = OffsetDateTime.parse(entity.created_at)
        translatedTimeEntry.updatedAt = OffsetDateTime.parse(entity.updated_at)
        return translatedTimeEntry
    }

    fun translate(entities: List<ForecastTimeEntry>): List<TimeEntry> {
        return entities.map(this::translate)
    }
}