package fi.metatavu.timebank.api.forecast.translate

import fi.metatavu.timebank.api.forecast.ForecastService
import fi.metatavu.timebank.api.forecast.models.ForecastProject
import fi.metatavu.timebank.api.forecast.models.ForecastTask
import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntry
import fi.metatavu.timebank.api.persistence.model.TimeEntry
import fi.metatavu.timebank.api.persistence.model.WorktimeCalendar
import fi.metatavu.timebank.api.utils.VacationUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * Translates ForecastTimeEntry object to persistable TimeEntry object
 */
@ApplicationScoped
class ForecastTimeEntryTranslator {

    /**
     * Translates ForecastTimeEntry into TimeEntry
     *
     * @param entity ForecastTimeEntry
     * @param worktimeCalendars List of WorktimeCalendars
     * @param forecastProjects List of ForecastProjects
     * @param forecastTasks List of ForecastTasks
     * @return TimeEntry
     */
    fun translate(
        entity: ForecastTimeEntry,
        worktimeCalendars: List<WorktimeCalendar>,
        forecastprojects: List<ForecastProject>,
        forecastTasks: List<ForecastTask>
    ): TimeEntry {
        val createdAt = LocalDateTime.parse(entity.createdAt.replace("Z", ""))
        val updatedAt = LocalDateTime.parse(entity.updatedAt.replace("Z", ""))
        val billableProject = forecastprojects.find { it.id == entity.project }?.budgetType != ForecastService.NON_BILLABLE
        val billableTask = forecastTasks.find { it.id == entity.task }?.unBillable
        val translatedTimeEntry = TimeEntry()
        translatedTimeEntry.entryId = UUID.randomUUID()
        translatedTimeEntry.forecastId = entity.id
        translatedTimeEntry.person = entity.person
        translatedTimeEntry.internalTime = if (entity.nonProjectTime != null) entity.timeRegistered else 0

        if (!billableTask!! && billableProject) {
            translatedTimeEntry.billableProjectTime = entity.timeRegistered
            translatedTimeEntry.nonBillableProjectTime = 0
        } else {
            translatedTimeEntry.billableProjectTime = 0
            translatedTimeEntry.nonBillableProjectTime = entity.timeRegistered
        }

        translatedTimeEntry.date = LocalDate.parse(entity.date)
        translatedTimeEntry.createdAt = createdAt.atZone(ZoneId.of("Europe/Helsinki")).toOffsetDateTime()
        translatedTimeEntry.updatedAt = updatedAt.atZone(ZoneId.of("Europe/Helsinki")).toOffsetDateTime()
        translatedTimeEntry.worktimeCalendar = worktimeCalendars.find { it.personId == entity.person }
        translatedTimeEntry.isVacation = entity.nonProjectTime == VacationUtils.VACATION_ID
        return translatedTimeEntry
    }

    /**
     * Translates list of ForecastTimeEntries
     *
     * @param entities list of ForecastTimeEntries to translate
     * @param worktimeCalendars List of WorktimeCalendars
     * @return List of TimeEntries
     */
    fun translate(
        entities: List<ForecastTimeEntry>,
        worktimeCalendars: List<WorktimeCalendar>,
        forecastProjects: List<ForecastProject>,
        forecastTasks: List<ForecastTask>
    ): List<TimeEntry> {
        return entities.map { entity ->
            translate(entity, worktimeCalendars, forecastProjects, forecastTasks)
        }
    }
}