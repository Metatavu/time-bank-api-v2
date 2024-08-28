package fi.metatavu.timebank.api.test.functional.data

import fi.metatavu.timebank.api.severa.models.SeveraUser
import fi.metatavu.timebank.api.severa.models.SeveraWorkContract

class TestUsersData {
    companion object {
        private var mockWorkContracts = listOf(
            createWorkContract(
                startDate = "2023-06-14",
                endDate = null
            ),
            createWorkContract(
                startDate = "2021-07-25",
                endDate = "2024-05-22"
            ),
            createWorkContract(
                startDate = "2020-07-25",
                endDate = "2022-05-22"
            ),
            createWorkContract(
                startDate = "2021-09-29",
                endDate = null
            )
        )

        private val mockUsers = listOf(
            createTestUser(
                guid = "1a",
                firstName = "FirstA",
                lastName = "LastA",
                email = "TesterA@example.com",
                severaWorkContract = mockWorkContracts[0],
                isActive = true
            ),
            createTestUser(
                guid = "2b",
                firstName = "FirstB",
                lastName = "LastB",
                email = "TesterB@example.com",
                severaWorkContract = mockWorkContracts[1],
                isActive = false
            ),
            createTestUser(
                guid = "3c",
                firstName = "FirstC",
                lastName = "LastC",
                email = "TesterC@example.com",
                severaWorkContract = mockWorkContracts[2],
                isActive = true
            ),
            createTestUser(
                guid = "4d",
                firstName = "FirstD",
                lastName = "LastD",
                email = "TesterD@example.com",
                severaWorkContract = mockWorkContracts[3],
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

        private fun createWorkContract(
            startDate: String,
            endDate: String?
        ): SeveraWorkContract {
            val newContract = SeveraWorkContract()
            newContract.startDate = startDate
            newContract.endDate = endDate

            return newContract
        }

        private fun createTestUser(
            guid: String,
            firstName: String,
            lastName: String,
            email: String,
            severaWorkContract: SeveraWorkContract,
            isActive: Boolean,
        ): SeveraUser {
            val newUser = SeveraUser()
            newUser.guid = guid
            newUser.firstName = firstName
            newUser.lastName = lastName
            newUser.email = email
            newUser.severaWorkContract = severaWorkContract
            newUser.isActive = isActive

            return newUser
        }
    }
}