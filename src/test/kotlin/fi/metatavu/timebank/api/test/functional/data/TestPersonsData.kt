package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getLastYearToday

/**
 * Class for test persons mock data
 */
class TestPersonsData {

    companion object {

        /**
         * Gets list of mock ForecastPersons
         *
         * @return List of ForecastPersons
         */
        fun getPersons(): List<ForecastPerson> {
            return listOf(
                createTestPerson(
                    id = 1,
                    firstName = "TesterA",
                    lastName = "TestA",
                    email = "testerA@example.com",
                    monday = 435,
                    tuesday = 435,
                    wednesday = 435,
                    thursday = 435,
                    friday = 435,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "ENGLISH_UK",
                    holidayCalendarId = 123456,
                    startDate = "2021-01-01",
                    createdAt = "2021-01-01",
                    isSystemUser = false
                ),
                createTestPerson(
                    id = 2,
                    firstName = "TesterB",
                    lastName = "TestB",
                    email = "testerc@example.com",
                    monday = 435,
                    tuesday = 435,
                    wednesday = 435,
                    thursday = 435,
                    friday = 435,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "ENGLISH_UK",
                    holidayCalendarId = 123456,
                    startDate = getLastYearToday().toString(),
                    createdAt = getLastYearToday().toString(),
                    isSystemUser = false
                ),
                createTestPerson(
                    id = 3,
                    firstName = "TesterC",
                    lastName = "TestC",
                    email = "testerd@example.com",
                    monday = 435,
                    tuesday = 435,
                    wednesday = 435,
                    thursday = 435,
                    friday = 435,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "FINNISH",
                    holidayCalendarId = 123456,
                    startDate = getLastYearToday().toString(),
                    createdAt = getLastYearToday().toString(),
                    isSystemUser = false
                ),
                createTestPerson(
                    id = 4,
                    firstName = "TEST_API_SYSTEM",
                    lastName = "",
                    monday = 0,
                    tuesday = 0,
                    wednesday = 0,
                    thursday = 0,
                    friday = 0,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "ENGLISH_UK",
                    holidayCalendarId = 123456,
                    startDate = getLastYearToday().toString(),
                    createdAt = getLastYearToday().toString(),
                    isSystemUser = true
                ),
                createTestPerson(
                    id = 5,
                    firstName = "TesterE",
                    lastName = "Updater",
                    email = "testerb@example.com",
                    monday = 435,
                    tuesday = 435,
                    wednesday = 435,
                    thursday = 435,
                    friday = 435,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "RUSSIAN",
                    holidayCalendarId = 123456,
                    startDate = getLastYearToday().toString(),
                    createdAt = getLastYearToday().toString(),
                    isSystemUser = false,
                )
            )
        }

        /**
         * Gets list of mock ForecastPersons
         * Mocks person whose expected worktimes has changed.
         *
         * @return List of ForecastPersons
         */
        fun getUpdatedPersons(): List<ForecastPerson> {
            return listOf(
                createTestPerson(
                    id = 5,
                    firstName = "TesterE",
                    lastName = "Updater",
                    monday = 217,
                    tuesday = 217,
                    wednesday = 217,
                    thursday = 217,
                    friday = 217,
                    saturday = 0,
                    sunday = 0,
                    active = true,
                    language = "RUSSIAN",
                    holidayCalendarId = 123456,
                    startDate = getLastYearToday().toString(),
                    createdAt = getLastYearToday().toString(),
                    isSystemUser = false
                )
            )
        }

        /**
         * Helper method for simplifying creating of ForecastPerson objects
         *
         * @param id id
         * @param firstName firstName
         * @param lastName lastName
         * @param monday monday expected worktime
         * @param tuesday tuesday expected worktime
         * @param wednesday wednesday expected worktime
         * @param thursday thursday expected worktime
         * @param friday friday expected worktime
         * @param saturday saturday expected worktime
         * @param sunday sunday expected worktime
         * @param active active
         * @param language language
         * @param holidayCalendarId holidayCalendarId
         * @param startDate startDate
         * @param createdAt createdAt
         * @param isSystemUser isSystemUser
         * @return ForecastPerson
         */
        private fun createTestPerson(
            id: Int,
            firstName: String,
            lastName: String,
            email: String = "",
            monday: Int,
            tuesday: Int,
            wednesday: Int,
            thursday: Int,
            friday: Int,
            saturday: Int,
            sunday: Int,
            active: Boolean,
            language: String,
            holidayCalendarId: Int,
            startDate: String,
            createdAt: String,
            isSystemUser: Boolean
        ): ForecastPerson {
            val newPerson = ForecastPerson()
            newPerson.id = id
            newPerson.firstName = firstName
            newPerson.lastName = lastName
            newPerson.email = email
            newPerson.monday = monday
            newPerson.tuesday = tuesday
            newPerson.wednesday = wednesday
            newPerson.thursday = thursday
            newPerson.friday = friday
            newPerson.saturday = saturday
            newPerson.sunday = sunday
            newPerson.active = active
            newPerson.language = language
            newPerson.holidayCalendarId = holidayCalendarId
            newPerson.startDate = startDate
            newPerson.createdAt = createdAt
            newPerson.isSystemUser = isSystemUser

            return newPerson
        }
    }
}