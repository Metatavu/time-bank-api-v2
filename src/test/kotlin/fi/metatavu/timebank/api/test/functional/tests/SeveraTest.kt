package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.severa.SeveraAccessTokenContainer
import fi.metatavu.timebank.api.severa.SeveraService
import fi.metatavu.timebank.api.severa.SeveraService.Companion.USERS_READ
import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.inject.Inject

@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SeveraTest: AbstractTest() {

    @Inject
    lateinit var severaAccessTokenContainer: SeveraAccessTokenContainer

    @Inject
    lateinit var severaService: SeveraService

    @Test
    fun generateAccessToken(){
        val severaAccessToken = severaAccessTokenContainer.getAccessToken(listOf(USERS_READ))

        assertEquals("exampleAccessToken123", severaAccessToken.accessToken)
        assertEquals(80000, severaAccessToken.refreshTokenExpiresIn)
        assertTrue(severaAccessToken.scope.contains(USERS_READ))
    }

    @Test
    fun getSeveraUsers(){
        val severaUsers = severaService.getUsers()

        assertEquals(severaUsers.size, 4)
        assertEquals(severaUsers[0].firstName, "FirstA")
        assertTrue(severaUsers[2].isActive)
        assertEquals(severaUsers[1].severaWorkContract?.startDate, "2021-07-25")
    }

    @Test
    fun findSeveraUser(){
        val severaUser = severaService.findUser("4d")

        assertEquals(severaUser.email, "TesterD@example.com")
        assertEquals(severaUser.severaWorkContract?.endDate, null)
        assertTrue(severaUser.isActive)
    }
}