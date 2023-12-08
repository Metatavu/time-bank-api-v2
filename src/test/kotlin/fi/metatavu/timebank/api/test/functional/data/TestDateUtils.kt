package fi.metatavu.timebank.api.test.functional.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Class for utility methods for Dates used in tests
 */
class TestDateUtils {

    companion object {

        /**
         * Gets given date as OffsetDateTime in timezone Z
         *
         * @return String OffsetDateTime
         */
        fun getODT(dateTime: LocalDateTime): String {
            return OffsetDateTime.from(dateTime.atOffset(ZoneOffset.ofHours(0))).toString()
        }

        /**
         * Gets LocalDate of thirty days ago
         *
         * @return LocalDate
         */
        fun getThirtyDaysAgo(): LocalDate {
            return LocalDate.now().minusDays(30).with(DayOfWeek.MONDAY)
        }

        /**
         * Gets first week Dates, starting from thirty days ago
         *
         * @return List of LocalDates
         */
        fun getThirtyDaysAgoFirstWeek(): List<LocalDate> {
            return listOf(
                getThirtyDaysAgo(),
                getThirtyDaysAgo().plusDays(1),
                getThirtyDaysAgo().plusDays(2),
                getThirtyDaysAgo().plusDays(3),
                getThirtyDaysAgo().plusDays(4),
                getThirtyDaysAgo().plusDays(5),
                getThirtyDaysAgo().plusDays(6)
            )
        }

        /**
         * Gets second week Dates, starting from thirty days ago
         *
         * @return List of LocalDates
         */
        fun getThirtyDaysAgoSecondWeek(): List<LocalDate> {
            return getThirtyDaysAgoFirstWeek().map { it.plusWeeks(1) }
        }

        /**
         * Gets third week Dates, starting from thirty days ago
         *
         * @return List of LocalDates
         */
        fun getThirtyDaysAgoThirdWeek(): List<LocalDate> {
            return getThirtyDaysAgoSecondWeek().map { it.plusWeeks(2) }
        }

        /**
         * Gets LocalDate of 60 days ago
         *
         * @return LocalDate
         */
        fun getSixtyDaysAgo(): LocalDate {
            return LocalDate.now().minusDays(60)
        }

        /**
         * Gets LocalDate of 365 days ago
         *
         * @return LocalDate
         */
        fun getLastYearToday(): LocalDate {
            return LocalDate.now().minusDays(365)
        }
    }
}