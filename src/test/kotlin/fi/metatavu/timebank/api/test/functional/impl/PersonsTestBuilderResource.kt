package fi.metatavu.timebank.api.test.functional.impl

import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.settings.ApiTestSettings
import fi.metatavu.timebank.test.client.apis.PersonsApi
import fi.metatavu.timebank.test.client.infrastructure.ApiClient
import fi.metatavu.timebank.test.client.infrastructure.ClientException
import fi.metatavu.timebank.test.client.infrastructure.ServerException
import fi.metatavu.timebank.test.client.models.Person
import fi.metatavu.timebank.test.client.models.PersonTotalTime
import fi.metatavu.timebank.test.client.models.Timespan
import org.junit.Assert

/**
 * Resource for testing Persons API
 */
class PersonsTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
): ApiTestBuilderResource<Person, ApiClient?>(testBuilder, apiClient) {

    // This test builder resource doesn't clean anything therefore this is empty.
    override fun clean(t: Person?) {
    }

    override fun getApi(): PersonsApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return PersonsApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Gets persons
     *
     * @param active whether person is active or not
     * @return list of persons
     */
    fun getPersons(active: Boolean? = true): Array<Person> {
        return api.listPersons(
            active = active
        )
    }

    /**
     * Gets total time entries of given person
     *
     * @param personId person id
     * @param timespan timespan
     * @return person's total time
     */
    fun getPersonTotal(personId: Int, timespan: Timespan? = Timespan.ALL_TIME, before: String? = null, after: String? = null): Array<PersonTotalTime> {
        return api.listPersonTotalTime(
            personId = personId,
            timespan = timespan,
            before = before,
            after = after
        )
    }

    /**
     * Updates given Person minimumBillableRate
     *
     * @param personId PersonId
     * @param person Person
     * @return person updated Person
     */
    fun updatePerson(personId: Int, person: Person): Person {
        return api.updatePerson(
            personId = personId,
            person = person
        )
    }

    /**
     * Asserts that updating Person minimumBillableRate fails with given status
     *
     * @param person Person
     * @param expectedStatus expected status code
     */
    fun assertUpdateFail(person: Person, expectedStatus: Int) {
        try {
            api.updatePerson(
                personId = person.id,
                person = person
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: java.lang.RuntimeException) {
            when (ex) {
                is ClientException -> assertClientExceptionStatus(expectedStatus, ex)
                is ServerException -> assertServerExceptionStatus(expectedStatus, ex)
            }
        }
    }

    /**
     * Asserts that listing persons fails with given status
     *
     * @param expectedStatus expected status code
     */
    fun assertListFail(expectedStatus: Int) {
        try {
            api.listPersons(
                active = null
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that getting total time entries of a given person fails with given status
     *
     * @param expectedStatus expected status code
     * @param personId person id
     */
    fun assertTotalsFail(expectedStatus: Int, personId: Int) {
        try {
            api.listPersonTotalTime(
                personId = personId,
                timespan = null,
                before = null,
                after = null
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }

    /**
     * Asserts that listing persons with a null token fails with given status
     *
     * @param expectedStatus expected status
     */
    fun assertListFailWithNullToken(expectedStatus: Int) {
        try {
            api.listPersons(
                active = null
            )
            Assert.fail(String.format("Expected fail with status, $expectedStatus"))
        } catch (ex: ClientException) {
            assertClientExceptionStatus(expectedStatus, ex)
        }
    }
}