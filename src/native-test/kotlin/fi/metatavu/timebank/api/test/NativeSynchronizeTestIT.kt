package fi.metatavu.timebank.api.test

import fi.metatavu.timebank.api.test.functional.resources.LocalTestProfile
import fi.metatavu.timebank.api.test.functional.resources.TestMySQLResource
import fi.metatavu.timebank.api.test.functional.resources.TestWiremockResource
import io.quarkus.test.junit.QuarkusIntegrationTest
import io.quarkus.test.common.QuarkusTestResource
import fi.metatavu.timebank.api.test.functional.tests.SynchronizeTest
import io.quarkus.test.junit.TestProfile

/**
 * Native tests for Synchronize API
 */
@QuarkusIntegrationTest
@QuarkusTestResource.List(
    QuarkusTestResource(TestMySQLResource::class),
    QuarkusTestResource(TestWiremockResource::class)
)
@TestProfile(LocalTestProfile::class)
class NativeSynchronizeTestIT: SynchronizeTest() {

}