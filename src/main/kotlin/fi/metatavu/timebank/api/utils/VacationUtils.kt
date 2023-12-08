package fi.metatavu.timebank.api.utils

import fi.metatavu.timebank.api.controllers.TimeEntryController
import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import java.time.LocalDate
import java.time.Period
import java.time.Year
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlin.math.roundToInt
import org.eclipse.microprofile.config.ConfigProvider

/**
 * Vacation Utils class
 */
@ApplicationScoped
class VacationUtils {

    @Inject
    lateinit var timeEntryController: TimeEntryController

    /**
     * Gets amount of unspent and spent vacations for person
     *
     * @param personId personId
     * @return Pair<Int, Int> (unspent, spent)
     */
    suspend fun getPersonsVacations(person: ForecastPerson): Pair<Int, Int> {
        if (person.startDate == "") return Pair(0, 0)

        val personStartDate = LocalDate.parse(person.startDate)
        val vacations = timeEntryController.getEntries(
            personId = person.id,
            before = null,
            after = if (personStartDate < vacationAccumulationStart) {
                        vacationAccumulationStart
                    } else {
                        personStartDate
                    },
            vacation = true
        )
        val spentVacations = vacations.size
        val unspentVacations: Int

        if (personStartDate < lastVacationAccumulationStart || personStartDate == LocalDate.parse("2021-07-31")) {
            unspentVacations = (12 * 2.5 - spentVacations).roundToInt()
        } else {
            var accumulatedVacationMonths = Period.between(personStartDate, vacationAccumulationEnd.plusDays(1)).months
            if (accumulatedVacationMonths < 0) accumulatedVacationMonths = 0
            unspentVacations = (accumulatedVacationMonths * 2.5 - spentVacations).roundToInt()
        }

        return Pair(unspentVacations, spentVacations)
    }

    companion object {
        val VACATION_ID = ConfigProvider.getConfig().getValue("forecast.vacation.id", Int::class.java) ?: 228255
        val vacationAccumulationStart = checkVacationAccumulationStart()
        val vacationAccumulationEnd = checkVacationAccumulationEnd()
        val lastVacationAccumulationStart = checkLastVacationAccumulationStart()

        /**
         * Gets the start of current vacation accumulation
         *
         * @return LocalDate
         */
        private fun checkVacationAccumulationStart(): LocalDate {
            return if (LocalDate.now().monthValue < 4) {
                LocalDate.of(Year.now().value - 1, 4, 1)
            } else {
                LocalDate.of(Year.now().value, 4, 1)
            }
        }

        /**
         * Gets the end of current vacation accumulation
         *
         * @return LocalDate
         */
        private fun checkVacationAccumulationEnd(): LocalDate {
            return if (LocalDate.now().monthValue < 4) {
                LocalDate.of(Year.now().value - 1, 3, 31)
            } else {
                LocalDate.of(Year.now().value , 3, 31)
            }
        }

        /**
         * Gets the start of last vacation accumulation
         *
         * @return LocalDate
         */
        private fun checkLastVacationAccumulationStart(): LocalDate {
            return if (LocalDate.now().monthValue < 4) {
                LocalDate.of(Year.now().value - 2, 4, 1)
            } else {
                LocalDate.of(Year.now().value - 1, 4, 1)
            }
        }
    }
}