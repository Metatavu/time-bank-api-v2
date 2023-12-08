package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.api.forecast.ForecastService
import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.forecast.models.ForecastTask
import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntry
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.slf4j.Logger

/**
 * Controller for synchronization Forecast time registrations with Time-bank
 */
@ApplicationScoped
class SynchronizeController {

    @Inject
    lateinit var worktimeCalendarController: WorktimeCalendarController

    @Inject
    lateinit var personsController: PersonsController

    @Inject
    lateinit var timeEntryController: TimeEntryController

    @Inject
    lateinit var forecastService: ForecastService

    @Inject
    lateinit var logger: Logger

    private val oldestSyncDate = LocalDate.parse("2021-07-30")

    /**
     * Synchronizes time entries from Forecast to Time-bank
     *
     * @param after YYYY-MM-DD LocalDate
     * @param syncDeletedEntries boolean if true synchronize deleted time entries between forecast and time bank
     */
    suspend fun synchronize(after: LocalDate? = oldestSyncDate, syncDeletedEntries: Boolean = false) {
        var forecastPersons = personsController.getPersonsFromForecast()

        forecastPersons.forEach { worktimeCalendarController.checkWorktimeCalendar(it) }

        forecastPersons = personsController.filterPersons(forecastPersons)

        val forecastTimeEntries = retrieveAllEntries(
            after = after,
            forecastPersons = forecastPersons
        )
        val forecastTasks = retrieveAllTasks()

        try {
            var synchronized = 0
            var duplicates = 0

            forecastTimeEntries.forEachIndexed { idx, timeEntry ->
                val person = forecastPersons.find { person -> person.id == timeEntry.person }
                val personName = "${person?.lastName}, ${person?.firstName}"
                logger.info("Synchronizing TimeEntry ${idx + 1}/${forecastTimeEntries.size} of $personName...")

                if (timeEntryController.createEntry(timeEntry, forecastTasks)) {
                    synchronized++
                    logger.info("Synchronized TimeEntry #${idx + 1}!")
                } else {
                    duplicates++
                    logger.warn("Time Entry ${idx + 1}/${forecastTimeEntries.size} already synchronized!")
                }
            }
            logger.info("Finished synchronization with: $synchronized entries synchronized... $duplicates entries NOT synchronized...")

            if (syncDeletedEntries) {
                val timeBankTimeEntries = timeEntryController.getEntries(personId = null, before = null, after = after, vacation = false)

                var deletedEntries = 0
                var synchronizedEntries = 0

                timeBankTimeEntries.forEachIndexed { _, timeEntry ->
                    if (timeEntry.forecastId != null) {
                        if (forecastTimeEntries.none { it.id == timeEntry.forecastId }) {
                            timeEntryController.deleteEntry(timeEntry.id)
                            logger.info("Deleted persisted entry ${timeEntry.id}")
                            deletedEntries++
                        }
                    }
                    synchronizedEntries++
                }
                logger.info("Went through $synchronizedEntries entries. Deleted entries: $deletedEntries")
            }
        } catch (e: Error) {
            logger.error("Error during synchronization: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Loops through paginated API responses of varying sizes from Forecast API
     * and translates the received ForecastTimeEntries to TimeEntries.
     *
     * @param after YYYY-MM-DD LocalDate
     * @param forecastPersons List of ForecastPersons
     * @return List of TimeEntries
     */
    private suspend fun retrieveAllEntries(after: LocalDate?, forecastPersons: List<ForecastPerson>): List<ForecastTimeEntry> {
        var retrievedAllEntries = false
        val forecastTimeEntries = mutableListOf<ForecastTimeEntry>()
        var pageNumber = 1

        while (!retrievedAllEntries) {

            val forecastTimeEntryResponse = forecastService.getTimeEntries(after, pageNumber)
            val amountOfPages =
                if (forecastTimeEntryResponse.totalObjectCount / forecastTimeEntryResponse.pageSize < 1) 1
                else forecastTimeEntryResponse.totalObjectCount / forecastTimeEntryResponse.pageSize
            logger.info("Retrieved page $pageNumber/$amountOfPages of time registrations from Forecast API!")

            if (pageNumber * forecastTimeEntryResponse.pageSize < forecastTimeEntryResponse.totalObjectCount) {
                pageNumber++
            } else {
                retrievedAllEntries = true
            }

            forecastTimeEntries.addAll(forecastTimeEntryResponse.pageContents!!)
        }

        return synchronizationDayValidator(
            timeEntries = forecastTimeEntries,
            persons = forecastPersons
        )
    }

    /**
     * Loops through paginated API responses of varying sizes from Forecast API to get all Tasks
     *
     * @return List of ForecastTasks
     */
    private suspend fun retrieveAllTasks(): List<ForecastTask> {
        var retrievedAllTasks = false
        val forecastTasks = mutableListOf<ForecastTask>()
        var pageNumber = 1

        while (!retrievedAllTasks) {

            val forecastTaskResponse = forecastService.getTasks(pageNumber = pageNumber)
            val amountOfPages =
                if (forecastTaskResponse.totalObjectCount / forecastTaskResponse.pageSize < 1) 1
                else forecastTaskResponse.totalObjectCount / forecastTaskResponse.pageSize
            logger.info("Retrieved page $pageNumber/$amountOfPages of tasks from Forecast API!")

            if (pageNumber * forecastTaskResponse.pageSize < forecastTaskResponse.totalObjectCount) {
                pageNumber++
            } else {
                retrievedAllTasks = true
            }

            forecastTasks.addAll(forecastTaskResponse.pageContents!!)
        }

        return forecastTasks
    }

    /**
     * Checks if each Person has TimeEntry for each day of synchronization.
     * If not, creates TimeEntry with zero logged
     *
     * @param timeEntries timeEntries
     * @param persons persons
     * @return List of ForecastTimeEntries
     */
    private suspend fun synchronizationDayValidator(timeEntries: List<ForecastTimeEntry>, persons: List<ForecastPerson>): List<ForecastTimeEntry> {
        val sortedEntries = timeEntries.sortedBy { it.date }.filter { LocalDate.parse(it.date) > oldestSyncDate }.toMutableList()
        var firstEntryDate = LocalDate.parse(sortedEntries.first().date)
        if (firstEntryDate < oldestSyncDate) firstEntryDate = oldestSyncDate

        persons.forEach { forecastPerson ->
            val personStartDate = LocalDate.parse(forecastPerson.startDate)
            val firstDate = if (personStartDate >= firstEntryDate) personStartDate else firstEntryDate
            val daysBetween =
                if (personStartDate >= firstEntryDate) {
                    ChronoUnit.DAYS.between(personStartDate, LocalDate.now())
                } else {
                    ChronoUnit.DAYS.between(firstEntryDate, LocalDate.now())
                }

            val personEntries = sortedEntries.filter { it.person == forecastPerson.id }

            for (dayNumber in 0..daysBetween) {
                val currentDate = firstDate.plusDays(dayNumber)

                if (personEntries.find { LocalDate.parse(it.date) == currentDate } == null) {
                    val existingEntry = timeEntryController.getEntries(
                        personId = forecastPerson.id,
                        before = currentDate,
                        after = currentDate,
                        vacation = null
                    )

                    if (existingEntry.isEmpty()) {
                        sortedEntries.add(
                            createForecastTimeEntry(
                                person = forecastPerson.id,
                                date = currentDate.toString(),
                                createdBy = forecastPerson.id,
                                updatedBy = forecastPerson.id,
                                createdAt = "${LocalDateTime.from(currentDate?.atStartOfDay())}Z",
                                updatedAt = "${LocalDateTime.from(currentDate?.atStartOfDay())}Z"
                            )
                        )
                    }
                }
            }
        }

        return sortedEntries
    }

    /**
     * Creates ForecastTimeEntry
     *
     * @param person person
     * @param date date
     * @param createdBy createdBy
     * @param updatedBy updatedBy
     * @param createdAt createdAt
     * @param updatedAt updatedAt
     * @return ForecastTimeEntry
     */
    private fun createForecastTimeEntry(
        person: Int,
        date: String,
        createdBy: Int,
        updatedBy: Int,
        createdAt: String,
        updatedAt: String
    ): ForecastTimeEntry {
        val newTimeEntry = ForecastTimeEntry()
        newTimeEntry.id = null
        newTimeEntry.person = person
        newTimeEntry.nonProjectTime = null
        newTimeEntry.timeRegistered = 0
        newTimeEntry.date = date
        newTimeEntry.createdBy = createdBy
        newTimeEntry.updatedBy = updatedBy
        newTimeEntry.createdAt = createdAt
        newTimeEntry.updatedAt = updatedAt

        return newTimeEntry
    }
}