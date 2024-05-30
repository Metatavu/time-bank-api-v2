package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.persistence.model.TimeEntry
import fi.metatavu.timebank.api.persistence.model.WorktimeCalendar
import java.time.LocalDate
import fi.metatavu.timebank.model.DailyEntry
import org.slf4j.Logger
import java.time.DayOfWeek
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for DailyEntry objects
 */
@ApplicationScoped
class DailyEntryController {

    @Inject
    lateinit var worktimeCalendarController: WorktimeCalendarController

    @Inject
    lateinit var timeEntryController: TimeEntryController

    @Inject
    lateinit var personsController: PersonsController

    @Inject
    lateinit var logger: Logger

    /**
     * Lists daily total times from combined timeEntries
     *
     * @param personId persons id in Forecast
     * @param before LocalDate to retrieve entries before given date
     * @param after LocalDate to retrieve entries after given date
     * @param vacation filter vacation days
     * @return List of DailyEntries
     */
    suspend fun list(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): List<DailyEntry>? {
        return makeDailyEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = vacation
        )
    }

    /**
     * Makes list of DailyEntries
     *
     * @param personId persons id in Forecast
     * @param before LocalDate to retrieve entries before given date
     * @param after LocalDate to retrieve entries after given date
     * @param vacation filter vacation days
     * @return List of DailyEntries
     */
    suspend fun makeDailyEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): List<DailyEntry> {
        return try {
            val persons = personsController.getPersonsFromForecast()
            val holidays = personsController.getHolidaysFromForecast()

            val entries = timeEntryController.getEntries(
                personId = personId,
                before = before,
                after = after,
                vacation = vacation
            )

            if (entries.isEmpty()) return emptyList()

            val dailyEntries = mutableListOf<DailyEntry>()

            persons.forEach { person ->
                val personsWorktimeCalendars = worktimeCalendarController.getAllWorktimeCalendarsByPerson(
                    personId = person.id
                )
                entries
                    .filter { timeEntry -> timeEntry.person == person.id }
                    .groupBy { it.date }.values.map { entriesOfDay ->
                        dailyEntries.add(calculateDailyEntries(
                            entriesOfDay = entriesOfDay,
                            personId = person.id,
                            holidays = holidays,
                            personsWorktimeCalendars = personsWorktimeCalendars
                        ))
                    }
            }

            dailyEntries.sortedByDescending { it.date }
        } catch (e: Error) {
            logger.error("Error during calculation of daily entries: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Totals TimeEntries to DailyEntries
     *
     * @param entriesOfDay List of TimeEntries
     * @param personId personId
     * @param holidays List of LocalDate
     * @param personsWorktimeCalendars List of WorktimeCalendars
     * @return DailyEntry
     */
    suspend fun calculateDailyEntries(entriesOfDay: List<TimeEntry>, personId: Int, holidays: List<LocalDate>, personsWorktimeCalendars: List<WorktimeCalendar>): DailyEntry {
        var internalTime = 0
        var billableProjectTime = 0
        var nonBillableProjectTime = 0
        val date = entriesOfDay.first().date!!
        val isVacation = entriesOfDay.any { it.isVacation!! }

        val worktimeCalendar = personsWorktimeCalendars.find {
            it.calendarStart!! <= date && it.calendarEnd == null ||
            it.calendarStart!! <= date && it.calendarEnd!! >= date
        } ?: if (date < LocalDate.parse("2021-07-31")) {
            worktimeCalendarController.getDefaultWorktimeCalendar()
        } else {
            throw Error("Couldn't find WorktimeCalendar for person $personId at $date!")
        }

        entriesOfDay.forEach{ entry ->
            internalTime += entry.internalTime ?: 0
            billableProjectTime += entry.billableProjectTime ?: 0
            nonBillableProjectTime += entry.nonBillableProjectTime ?: 0
        }

        val expected = getDailyExpected(
            worktimeCalendar = worktimeCalendar,
            holidays = holidays,
            day = date
        )

        val loggedProjectTime = billableProjectTime + nonBillableProjectTime

        return DailyEntry(
            person = personId,
            internalTime = internalTime,
            billableProjectTime = billableProjectTime,
            nonBillableProjectTime = nonBillableProjectTime,
            logged = internalTime + loggedProjectTime,
            loggedProjectTime = loggedProjectTime,
            expected = expected,
            balance = internalTime + loggedProjectTime - expected,
            date = date,
            isVacation = isVacation
        )
    }

    /**
     * Gets persons expected worktime (in minutes) per day
     *
     * @param worktimeCalendar WorkTimeCalendar
     * @param holidays List of LocalDate
     * @param day LocalDate
     * @return Int minutes of expected work
     */
    private suspend fun getDailyExpected(worktimeCalendar: WorktimeCalendar, holidays: List<LocalDate>, day: LocalDate): Int {
        if (holidays.contains(day)) return 0

        return when (day.dayOfWeek) {
            DayOfWeek.MONDAY -> worktimeCalendar.monday!!
            DayOfWeek.TUESDAY-> worktimeCalendar.tuesday!!
            DayOfWeek.WEDNESDAY -> worktimeCalendar.wednesday!!
            DayOfWeek.THURSDAY -> worktimeCalendar.thursday!!
            DayOfWeek.FRIDAY -> worktimeCalendar.friday!!
            DayOfWeek.SATURDAY -> worktimeCalendar.saturday!!
            DayOfWeek.SUNDAY -> worktimeCalendar.sunday!!
            else -> throw Error("An unexpected date!")
        }
    }
}