package fi.metatavu.timebank.api.controllers

import fi.metatavu.timebank.model.PersonTotalTime
import fi.metatavu.timebank.api.forecast.ForecastService
import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.keycloak.KeycloakController
import fi.metatavu.timebank.api.utils.VacationUtils
import org.slf4j.Logger
import fi.metatavu.timebank.model.DailyEntry
import fi.metatavu.timebank.model.Person
import fi.metatavu.timebank.model.Timespan
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Controller for Person objects
 */
@ApplicationScoped
class PersonsController {

    @Inject
    lateinit var forecastService: ForecastService

    @Inject
    lateinit var dailyEntryController: DailyEntryController

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var vacationUtils: VacationUtils

    @Inject
    lateinit var keycloakController: KeycloakController

    /**
     * Updates Person minimumBillableRate in Keycloak
     *
     * @param person Person
     * @return person Person
     */
    suspend fun updatePerson(person: Person): Person {
        if (person.minimumBillableRate > 100 || person.minimumBillableRate < 0) {
            throw Error("Invalid minimumBillableRate!")
        }

        val keycloakUser = keycloakController.getUsersResource()?.list()?.find { it.email == person.email.lowercase() }
            ?: throw Error("Invalid e-mail!")

        keycloakController.updateUsersMinimumBillableRate(keycloakUser, person.minimumBillableRate)

        return Person(
            id = person.id,
            firstName = person.firstName,
            lastName = person.firstName,
            email = person.email,
            monday = person.monday,
            tuesday = person.tuesday,
            wednesday = person.wednesday,
            thursday = person.thursday,
            friday = person.friday,
            saturday = person.saturday,
            sunday = person.sunday,
            active = person.active,
            unspentVacations = person.unspentVacations,
            spentVacations = person.spentVacations,
            minimumBillableRate = keycloakController.getUsersMinimumBillableRate(keycloakUser),
            language = person.language,
            startDate = person.startDate,
            keycloakId = person.keycloakId
        )
    }

    /**
     * List persons data from Forecast API
     * Filters out system users since they should not be needed in this application
     *
     * @return List of ForecastPersons
     */
    suspend fun getPersonsFromForecast(): List<ForecastPerson> {
        return try {
            forecastService.getPersons().filter { !it.isSystemUser }
        } catch (e: Error) {
            logger.error("Error when requesting persons from Forecast API: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * List national holidays from Forecast API
     *
     * @return List of LocalDate
     */
    suspend fun getHolidaysFromForecast(): List<LocalDate> {
        return try {
            val forecastHolidays = forecastService.getHolidays()
            forecastHolidays.map{ holiday ->
                LocalDate.of(holiday.year, holiday.month, holiday.day)
            }
        } catch (e: Error) {
            logger.error("Error when requesting holidays from Forecast API: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Filters inactive Forecast persons and clients
     *
     * @param persons List of ForecastPersons
     * @return List of Forecast persons
     */
    fun filterPersons(persons: List<ForecastPerson>): List<ForecastPerson> {
        return persons.filter{ person -> person.active &&  person.clientId == null }
    }

    /**
     * Lists Forecast persons based on optional query parameters
     *
     * @param active
     * @return List of Forecast persons
     */
    suspend fun listPersons(active: Boolean? = true): List<ForecastPerson>? {
        val persons = getPersonsFromForecast()

        persons.forEach { forecastPerson ->
            val vacationAmounts = vacationUtils.getPersonsVacations(forecastPerson)
            forecastPerson.unspentVacations = vacationAmounts.first
            forecastPerson.spentVacations = vacationAmounts.second
        }

        return if (active == false) {
            persons
        } else {
            filterPersons(persons)
        }
    }

    /**
     * Makes List of PersonTotalTimes
     *
     * @param personId personId
     * @param timespan Timespan
     * @return List of PersonTotalTimes
     */
    suspend fun makePersonTotal(personId: Int, timespan: Timespan, before: LocalDate?, after: LocalDate?): List<PersonTotalTime>? {
        val dailyEntries = dailyEntryController.makeDailyEntries(
            personId = personId,
            before = before,
            after = after,
            vacation = null
        ) ?: return null

        return when (timespan) {
            Timespan.ALL_TIME -> {
                listOf(calculatePersonTotalTime(
                    personId = personId,
                    days = dailyEntries,
                    timespan = timespan
                )
                )
            }
            Timespan.YEAR -> {
                dailyEntries.groupBy{ it.date.year }.values.map{ days ->
                    calculatePersonTotalTime(
                        personId = personId,
                        days = days,
                        timespan = timespan
                    )
                }
            }
            Timespan.MONTH -> {
                dailyEntries.groupBy{ Pair(it.date.year, it.date.monthValue) }.values.map{ days ->
                    calculatePersonTotalTime(
                        personId = personId,
                        days = days,
                        timespan = timespan
                    )
                }
            }
            Timespan.WEEK -> {
                val weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 7).weekOfYear()
                dailyEntries.groupBy{ Pair(it.date.year, it.date.get(weekOfYear)) }.values.map{ days ->
                    calculatePersonTotalTime(
                        personId = personId,
                        days = days,
                        timespan = timespan
                    )
                }
            }
        }
    }

    /**
     * Totals DailyEntries to PersonTotalTimes based on timespan
     *
     * @param personId personId
     * @param days List of DailyEntries
     * @param timespan timespan
     * @return PersonTotalTime
     */
    private suspend fun calculatePersonTotalTime(personId: Int, days: List<DailyEntry>, timespan: Timespan): PersonTotalTime {
        val weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 7).weekOfYear()
        var internalTime = 0
        var billableProjectTime = 0
        var nonBillableProjectTime = 0
        var expected = 0
        var year: Int? = null
        var month: Int? = null
        var week: Int? = null
        var startDate: LocalDate? = null
        var endDate: LocalDate? = null

        days.forEachIndexed{ idx, day ->
            internalTime += day.internalTime
            billableProjectTime += day.billableProjectTime
            nonBillableProjectTime += day.nonBillableProjectTime
            expected += day.expected
            year = if (timespan != Timespan.ALL_TIME) day.date.year else null
            month = if (timespan == Timespan.MONTH || timespan == Timespan.WEEK) day.date.monthValue else null
            week = if (timespan == Timespan.WEEK) day.date.get(weekOfYear) else null
            if (idx == 0) endDate = day.date
            if (idx == days.lastIndex) startDate = day.date
        }

        val loggedProjectTime = billableProjectTime + nonBillableProjectTime
        val timePeriod = timespanDateStringBuilder(
            timespan = timespan,
            year = year,
            month = month,
            week = week,
            startDate = startDate,
            endDate = endDate
        )

        return PersonTotalTime(
            balance = internalTime + loggedProjectTime - expected,
            logged = internalTime + loggedProjectTime,
            loggedProjectTime = loggedProjectTime,
            expected = expected,
            internalTime = internalTime,
            billableProjectTime = billableProjectTime,
            nonBillableProjectTime = nonBillableProjectTime,
            personId = personId,
            timePeriod = timePeriod
        )
    }

    /**
     * Build string for the timespan date
     *
     * @param timespan of totals to display
     * @param Year of current time period
     * @param Month of current time period
     * @param Week of current time period
     * @param startDate date
     * @param endDate date
     * @return String timeperiod of given PersonTotalTime
     */
    private fun timespanDateStringBuilder(timespan: Timespan, year: Int?, month: Int?, week: Int?, startDate: LocalDate?, endDate: LocalDate?): String {
        return when (timespan) {
            Timespan.ALL_TIME -> "$startDate,$endDate"
            Timespan.YEAR -> "$year"
            Timespan.MONTH -> "$year,$month"
            Timespan.WEEK -> "$year,$month,$week"
        }
    }
}