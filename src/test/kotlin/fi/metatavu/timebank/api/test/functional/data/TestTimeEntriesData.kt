package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntry
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getLastYearToday
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getODT
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getSixtyDaysAgo
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoFirstWeek
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoSecondWeek
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgoThirdWeek
import java.time.LocalDate
import java.time.LocalDateTime

/**
* Class for test time entries mock data
*/
class TestTimeEntriesData {

    companion object {

        /**
         * Gets list of mock ForecastTimeEntries
         *
         * @return List of ForecastTimeEntries
         */
        fun getForecastTimeEntries(): List<ForecastTimeEntry> {
            val entries = mutableListOf(
                createTestTimeEntry(
                    id = 0,
                    person = 2,
                    task = 123,
                    nonProjectTime = 123,
                    timeRegistered = 435,
                    date = getLastYearToday().toString(),
                    createdAt = getODT(getLastYearToday().atStartOfDay()),
                    updatedAt = getODT(getLastYearToday().atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 1,
                    person = 1,
                    task = 123,
                    nonProjectTime = 255455,
                    timeRegistered = 100,
                    date = getThirtyDaysAgoFirstWeek().first().toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek().first().atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek().first().atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 2,
                    person = 1,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 100,
                    date = getThirtyDaysAgoFirstWeek()[1].toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek()[1].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 3,
                    person = 2,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 400,
                    date = getThirtyDaysAgoFirstWeek()[2].toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek()[2].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek()[2].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 4,
                    person = 2,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 400,
                    date = getThirtyDaysAgoFirstWeek()[3].toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek()[3].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek()[3].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 5,
                    person = 3,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 122,
                    date = getThirtyDaysAgoFirstWeek()[4].toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek()[4].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek()[4].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 6,
                    person = 3,
                    task = 123,
                    nonProjectTime = 255455,
                    timeRegistered = 372,
                    date = getThirtyDaysAgoFirstWeek()[5].toString(),
                    createdAt = getODT(getThirtyDaysAgoFirstWeek()[5].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoFirstWeek()[5].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 7,
                    person = 3,
                    task = 123,
                    nonProjectTime = 114753,
                    timeRegistered = 378,
                    date = getThirtyDaysAgoSecondWeek().first().toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek().first().atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek().first().atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 8,
                    person = 3,
                    task = 789,
                    nonProjectTime = null,
                    timeRegistered = 122,
                    date = getThirtyDaysAgoSecondWeek()[1].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[1].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 9,
                    person = 3,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 52,
                    date = getThirtyDaysAgoSecondWeek()[2].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[2].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[2].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 10,
                    person = 1,
                    task = 123,
                    nonProjectTime = 12,
                    timeRegistered = 120,
                    date = getThirtyDaysAgoSecondWeek()[3].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[3].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[3].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 11,
                    person = 2,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 400,
                    date = getThirtyDaysAgoSecondWeek()[4].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[4].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[4].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 12,
                    person = 1,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 312,
                    date = getThirtyDaysAgoSecondWeek()[5].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[5].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[5].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 13,
                    person = 2,
                    task = null,
                    nonProjectTime = 228255,
                    timeRegistered = 213,
                    date = getThirtyDaysAgoSecondWeek()[6].toString(),
                    createdAt = getODT(getThirtyDaysAgoSecondWeek()[6].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoSecondWeek()[6].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 14,
                    person = 5,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 435,
                    date = getThirtyDaysAgoThirdWeek().first().toString(),
                    createdAt = getODT(getThirtyDaysAgoThirdWeek().first().atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek().first().atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 15,
                    person = 1,
                    task = 123,
                    nonProjectTime = 228255,
                    timeRegistered = 435,
                    date = getThirtyDaysAgoThirdWeek()[1].toString(),
                    createdAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay()),
                    updatedAt = getODT(getThirtyDaysAgoThirdWeek()[1].atStartOfDay())
                ),
                createTestTimeEntry(
                    id = 16,
                    person = 1,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 42,
                    date = getSixtyDaysAgo().toString(),
                    createdAt = getODT(getSixtyDaysAgo().atStartOfDay()),
                    updatedAt = getODT(getSixtyDaysAgo().atStartOfDay())
                )
            )
            entries.addAll(createTodaysEntries(entries.last().id!!))

            return entries
        }

        /**
         * Gets list of mock ForecastTimeEntries.
         * Mocks that an existing entry has been updated.
         *
         * @return List of ForecastTimeEntries
         */
        fun getUpdatedForecastTimeEntry(): List<ForecastTimeEntry> {
            return listOf(
                createTestTimeEntry(
                    id = 14,
                    person = 5,
                    task = 123,
                    nonProjectTime = 789,
                    timeRegistered = 435,
                    date = LocalDate.now().toString(),
                    createdAt = getODT(LocalDateTime.now()),
                    updatedAt = getODT(LocalDateTime.now().plusHours(3))
                )
            )
        }

        /**
         * Gets list of mock ForecastTimeEntries.
         * Contains entry for person whose expected worktime has changed.
         *
         * @return List of ForecastTimeEntries
         */
        fun getForecastTimeEntryForUpdatedPerson(): List<ForecastTimeEntry> {
            return listOf(
                createTestTimeEntry(
                    id = 17,
                    person = 5,
                    task = 123,
                    nonProjectTime = 789,
                    timeRegistered = 435,
                    date = LocalDate.now().toString(),
                    createdAt = getODT(LocalDate.now().atStartOfDay()),
                    updatedAt = getODT(LocalDate.now().atStartOfDay().plusHours(1))
                )
            )
        }

        /**
         * Makes a mock ForecastTimeEntry for each active person for current day
         *
         * @return List of mock ForecastTimeEntries
         */
        private fun createTodaysEntries(highestId: Int): List<ForecastTimeEntry> {
            val persons = TestPersonsData.getPersons().filter { it.active && !it.isSystemUser }

            return persons.mapIndexed { index, person ->
                val currentId = highestId + index + 1
                createTestTimeEntry(
                    id = currentId,
                    person = person.id,
                    task = 123,
                    nonProjectTime = null,
                    timeRegistered = 300,
                    date = LocalDate.now().toString(),
                    createdAt = getODT(LocalDate.now().atStartOfDay()),
                    updatedAt = getODT(LocalDate.now().atStartOfDay().plusHours(1))
                )
            }
        }


        /**
         * Helper method for simplifying creating of ForecastTimeEntry objects
         *
         * @param id id
         * @param person person
         * @param nonProjectTime nonProjectTime
         * @param timeRegistered timeRegistered
         * @param date date
         * @param createdAt createdAt
         * @param updatedAt
         * @return ForecastTimeEntry
         */
        private fun createTestTimeEntry(
            id: Int,
            person: Int,
            task: Int?,
            nonProjectTime: Int?,
            timeRegistered: Int,
            date: String,
            createdAt: String,
            updatedAt: String
        ): ForecastTimeEntry {
            val newTimeEntry = ForecastTimeEntry()
            newTimeEntry.id = id
            newTimeEntry.person = person
            newTimeEntry.task = task
            newTimeEntry.nonProjectTime = nonProjectTime
            newTimeEntry.timeRegistered = timeRegistered
            newTimeEntry.date = date
            newTimeEntry.createdBy = person
            newTimeEntry.updatedBy = person
            newTimeEntry.createdAt = createdAt
            newTimeEntry.updatedAt = updatedAt

            return newTimeEntry
        }
    }
}