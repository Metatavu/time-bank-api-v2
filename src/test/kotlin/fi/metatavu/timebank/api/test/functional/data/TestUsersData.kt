package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.severa.models.SeveraUser

class TestUsersData {
    companion object {
        private val mockUsers = listOf(
            createTestUser(
                guid = "1a",
                firstName = "FirstA",
                lastName = "LastA",
                email = "TesterA@example.com",
                startDate = "2023-06-14",
                endDate = null,
                isActive = true
            ),
            createTestUser(
                guid = "2b",
                firstName = "FirstB",
                lastName = "LastB",
                email = "TesterB@example.com",
                startDate = "2021-07-25",
                endDate = "2024-05-22",
                isActive = false
            ),
            createTestUser(
                guid = "3c",
                firstName = "FirstC",
                lastName = "LastC",
                email = "TesterC@example.com",
                startDate = "2024-05-06",
                endDate = "2024-10-08",
                isActive = true
            ),
            createTestUser(
                guid = "4d",
                firstName = "FirstD",
                lastName = "LastD",
                email = "TesterD@example.com",
                startDate = "2019-09-19",
                endDate = null,
                isActive = true
            )
        )

        /**
         * Returns a list of mock SeveraUser -objects
         *
         * @return List of SeveraUser
         */
        fun getUsers(): List<SeveraUser> {
            return mockUsers
        }

        /**
         * Finds mock SeveraUser based on guid
         *
         * @param guid String
         * @return mock SeveraUser
         */
        fun findUser(guid: String): SeveraUser {
            val mockUser = mockUsers.find { it.guid == guid}

            return mockUser!!
        }

        private fun createTestUser(
            guid: String,
            firstName: String,
            lastName: String,
            email: String,
            startDate: String,
            endDate: String?,
            isActive: Boolean,
        ): SeveraUser {
            val newUser = SeveraUser()
            newUser.guid = guid
            newUser.firstName = firstName
            newUser.lastName = lastName
            newUser.email = email
            newUser.startDate = startDate
            newUser.endDate = endDate
            newUser.isActive = isActive

            return newUser
        }
    }
}