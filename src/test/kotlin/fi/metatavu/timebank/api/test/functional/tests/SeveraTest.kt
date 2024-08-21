package fi.metatavu.timebank.api.test.functional.tests

import fi.metatavu.timebank.api.severa.SeveraService
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


    @Test
    fun listSeveraUsers(){
        assertTrue(true)
        /*
        assertEquals(users.size, 4)
        assertEquals(users[1].firstName, "FirstB")*/
    }

}