package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.persistence.model.WorktimeCalendar
import fi.metatavu.timebank.api.persistence.repositories.WorktimeCalendarRepository
import java.time.LocalDate
import java.util.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Controller for WorktimeCalendars
 */
@ApplicationScoped
class WorktimeCalendarController {

    @Inject
    lateinit var worktimeCalendarRepository: WorktimeCalendarRepository

    /**
     * Gets all WorktimeCalendars for given Person
     *
     * @param personId personId
     * @return List of WorktimeCalendars
     */
    suspend fun getAllWorktimeCalendarsByPerson(personId: Int): List<WorktimeCalendar> {
        return worktimeCalendarRepository.getAllWorkTimeCalendarsByPerson(
            personId = personId
        )!!
    }

    /**
     * Checks if persisted WorktimeCalendar is up-to-date for given person.
     * If not, ends old WorktimeCalendar and starts a new one with updated worktimes.
     *
     * @param person ForecastPerson
     */
    suspend fun checkWorktimeCalendar(person: ForecastPerson) {
        val allWorktimeCalendars = worktimeCalendarRepository.getAllWorkTimeCalendarsByPerson(person.id)
        if (allWorktimeCalendars.isNullOrEmpty()) {
            createWorktimeCalendar(
                person = person,
                first = true
            )

            return
        }

        val upToDateWorktimeCalendar = allWorktimeCalendars.find { it.calendarEnd == null }!!

        if (!compareExpected(person, upToDateWorktimeCalendar)) {
            updateWorktimeCalendar(
                id = upToDateWorktimeCalendar.id!!,
                calendarEnd = LocalDate.now().minusDays(1)
            )
        }
    }

    /**
     * Compares most up-to-date WorktimeCalendar with
     * expected worktimes retrieved from Forecast.
     * Returns true if WorktimeCalendar is up-to-date
     *
     * @param person Person
     * @param worktimeCalendar WorktimeCalendar
     * @return Boolean whether WorktimeCalendar was up-to-date
     */
    suspend fun compareExpected(person: ForecastPerson, worktimeCalendar: WorktimeCalendar): Boolean {
        val isCalendarUpToDate =
            person.monday == worktimeCalendar.monday &&
            person.tuesday == worktimeCalendar.tuesday &&
            person.wednesday == worktimeCalendar.wednesday &&
            person.thursday == worktimeCalendar.thursday &&
            person.friday == worktimeCalendar.friday &&
            person.saturday == worktimeCalendar.saturday &&
            person.sunday == worktimeCalendar.sunday

        return if (isCalendarUpToDate) {

            true
        } else {
            createWorktimeCalendar(
                person = person,
                first = false
            )

            false
        }
    }

    /**
     * Creates new WorktimeCalendar for given person
     *
     * @param person Person
     * @param first whether this is the first WorktimeCalendar for given Person
     * @return WorktimeCalendar
     */
    suspend fun createWorktimeCalendar(person: ForecastPerson, first: Boolean): WorktimeCalendar {
        val newWorktimeCalendar = WorktimeCalendar(
            id = UUID.randomUUID(),
            personId = person.id,
            monday = person.monday,
            tuesday = person.tuesday,
            wednesday = person.wednesday,
            thursday = person.thursday,
            friday = person.friday,
            saturday = person.saturday,
            sunday = person.sunday,
            calendarStart = if (first) LocalDate.parse(person.startDate) else LocalDate.now()
        )
        worktimeCalendarRepository.persistWorktimeCalendar(newWorktimeCalendar)

        return newWorktimeCalendar
    }

    /**
     * Updates (e.g. ends) given WorktimeCalendar
     *
     * @param id id
     * @param calendarEnd calendarEnd
     */
    suspend fun updateWorktimeCalendar(id: UUID, calendarEnd: LocalDate) {
        worktimeCalendarRepository.updateWorktimeCalendar(
            id = id,
            calendarEnd = calendarEnd
        )
    }

    /**
     * Returns "default" WorktimeCalendar
     * Used if for some reason TimeEntries with too old dates ends up in the system.
     */
    fun getDefaultWorktimeCalendar(): WorktimeCalendar {
        return WorktimeCalendar(
            monday = 0,
            tuesday = 0,
            wednesday = 0,
            thursday = 0,
            friday = 0,
            saturday = 0,
            sunday = 0
        )
    }
}