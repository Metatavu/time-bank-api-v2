package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.severa.SeveraAccessTokenContainer
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
class SeveraTokenTest: AbstractTest() {

    @Inject
    lateinit var severaAccessTokenContainer: SeveraAccessTokenContainer

    @Test
    fun generateAccessToken(){
        val severaAccessToken = severaAccessTokenContainer.getAccessToken(listOf(USERS_READ))

        assertEquals("exampleAccessToken123", severaAccessToken.accessToken)
        assertEquals(80000, severaAccessToken.refreshTokenExpiresIn)
        assertTrue(severaAccessToken.scope.contains(USERS_READ))
    }
}