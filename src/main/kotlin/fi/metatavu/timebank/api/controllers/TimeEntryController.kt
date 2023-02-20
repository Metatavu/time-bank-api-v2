package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.forecast.models.ForecastTask
import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntry
import fi.metatavu.timebank.api.persistence.model.TimeEntry
import fi.metatavu.timebank.api.persistence.repositories.TimeEntryRepository
import fi.metatavu.timebank.api.utils.VacationUtils
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for timeEntry objects
 */
@ApplicationScoped
class TimeEntryController {

    @Inject
    lateinit var timeEntryRepository: TimeEntryRepository

    /**
     * Gets persisted TimeEntries
     *
     * @param personId personId
     * @param before before
     * @param after after
     * @param vacation vacation
     * @return List of TimeEntries
     */
    suspend fun getEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): List<TimeEntry> {
        return timeEntryRepository.getEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = vacation
        )
    }

    /**
     * Creates and persists new TimeEntry
     *
     * @param forecastTimeEntry ForecastTimeEntry
     * @param forecastTasks List of ForecastTasks
     * @return boolean whether operation was successful
     */
    suspend fun createEntry(
        forecastTimeEntry: ForecastTimeEntry, forecastTasks: List<ForecastTask>): Boolean {
        val nonBillableTask = forecastTasks.find { it.id == forecastTimeEntry.task }?.unBillable ?: true
        val internalTime = forecastTimeEntry.nonProjectTime != null
        val newTimeEntry = TimeEntry()
        newTimeEntry.id = UUID.randomUUID()
        newTimeEntry.forecastId = forecastTimeEntry.id
        newTimeEntry.person = forecastTimeEntry.person
        newTimeEntry.internalTime = if (internalTime) forecastTimeEntry.timeRegistered else 0
        newTimeEntry.billableProjectTime = 0
        newTimeEntry.nonBillableProjectTime = 0

        if (!nonBillableTask && !internalTime) {
            newTimeEntry.billableProjectTime = forecastTimeEntry.timeRegistered
        } else if (nonBillableTask && !internalTime) {
            newTimeEntry.nonBillableProjectTime = forecastTimeEntry.timeRegistered
        }

        newTimeEntry.date = LocalDate.parse(forecastTimeEntry.date)
        newTimeEntry.createdAt = OffsetDateTime.parse(forecastTimeEntry.createdAt)
        newTimeEntry.updatedAt = OffsetDateTime.parse(forecastTimeEntry.updatedAt)
        newTimeEntry.isVacation = forecastTimeEntry.nonProjectTime == VacationUtils.VACATION_ID

        return timeEntryRepository.persistEntry(newTimeEntry)
    }

    /**
     * Deletes given persisted TimeEntry
     *
     * @param id id
     */
    suspend fun deleteEntry(id: UUID) {
        timeEntryRepository.deleteEntry(id = id)
    }

    suspend fun deleteEntry(forecastId: Int) {
        timeEntryRepository.deleteEntry(forecastId = forecastId)
    }
}