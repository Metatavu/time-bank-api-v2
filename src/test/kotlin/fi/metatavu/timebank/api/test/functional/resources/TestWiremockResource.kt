package fi.metatavu.timebank.api.test.functional.resources

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import fi.metatavu.timebank.api.test.functional.data.TestData
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.PERSONS_SCENARIO
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.HOLIDAYS_SCENARIO
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.TIMES_SCENARIO
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.ERROR_STATE
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.UPDATE_STATE_ONE
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.UPDATE_STATE_TWO
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getSixtyDaysAgo
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils.Companion.getThirtyDaysAgo
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.TASKS_SCENARIO
import fi.metatavu.timebank.api.test.functional.tests.AbstractTest.Companion.YEAR_STATE
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Wiremock to mock Forecast API
 */
class TestWiremockResource: QuarkusTestResourceLifecycleManager {
    private var wireMockServer = WireMockServer()
    private val objectMapper = ObjectMapper()

    data class ForecastErrorResponse(val status: Int, val message: String)

    override fun start(): Map<String, String> {
        objectMapper.findAndRegisterModules()
        wireMockServer = WireMockServer(8082)
        wireMockServer.start()
        configureFor("localhost", 8082)

        personsStubs(wireMockServer)
        holidayCalendarStubs(wireMockServer)
        timeRegistrationStubs(wireMockServer)
        tasksStubs(wireMockServer)

        return mapOf(
            "forecast.base.url" to  wireMockServer.baseUrl(),
            "forecast.api.key" to "noapikey"
        )
    }

    /**
     * /v2/persons -stubs
     *
     * @param wireMockServer WireMockServer
     */
    private fun personsStubs(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v2/persons"))
                .inScenario(PERSONS_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getPersons()), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v2/persons"))
                .inScenario(PERSONS_SCENARIO)
                .whenScenarioStateIs(ERROR_STATE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ForecastErrorResponse(
                    status = 401,
                    message = "Server failed to authenticate the request."
                )), 401))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v2/persons"))
                .inScenario(PERSONS_SCENARIO)
                .whenScenarioStateIs(UPDATE_STATE_ONE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getUpdatedPersons()), 200))
        )
    }

    /**
     * /v1/holiday_calendar_entries -stubs
     *
     * @param wireMockServer WireMockServer
     */
    private fun holidayCalendarStubs(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v1/holiday_calendar_entries"))
                .inScenario(HOLIDAYS_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getHolidays()), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v1/holiday_calendar_entries"))
                .inScenario(HOLIDAYS_SCENARIO)
                .whenScenarioStateIs(ERROR_STATE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ForecastErrorResponse(
                    status = 401,
                    message = "Server failed to authenticate the request."
                )), 401))
        )
    }

    /**
     * /v4/time_registrations -stubs
     *
     * @param wireMockServer WireMockServer
     */
    private fun timeRegistrationStubs(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponse(
                    before = null,
                    after = null
                )), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(YEAR_STATE)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponse()), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(ERROR_STATE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ForecastErrorResponse(
                    status = 401,
                    message = "Failed to authenticate the request"
                )), 401))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations/updated_after/${getPathParamDate(LocalDate.now().minusDays(1))}"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(UPDATE_STATE_TWO)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getUpdatedForecastTimeEntryResponse()), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations/updated_after/${getPathParamDate(LocalDate.now())}"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(UPDATE_STATE_ONE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponseForUpdatedPerson()), 200))
        )

        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations/updated_after/${getPathParamDate(getThirtyDaysAgo())}"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponse(
                    before = null,
                    after = getThirtyDaysAgo()
                )), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations/updated_after/${getPathParamDate(getSixtyDaysAgo())}"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponse(
                    before = null,
                    after = getSixtyDaysAgo()
                )), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/time_registrations"))
                .inScenario(TIMES_SCENARIO)
                .whenScenarioStateIs("delete")
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTimeEntryResponse()), 200))
        )
    }

    /**
     * /v4/tasks -stubs
     *
     * @param wireMockServer WireMockServer
     */
    private fun tasksStubs(wireMockServer: WireMockServer) {
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/tasks"))
                .inScenario(TASKS_SCENARIO)
                .whenScenarioStateIs(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(TestData.getForecastTaskResponse()), 200))
        )
        wireMockServer.stubFor(
            get(urlPathEqualTo("/v4/tasks"))
                .inScenario(TASKS_SCENARIO)
                .whenScenarioStateIs(ERROR_STATE)
                .willSetStateTo(STARTED)
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ForecastErrorResponse(
                    status = 401,
                    message = "Failed to authenticate the request"
                )), 401))
        )
    }

    /**
     * Converts given LocalDate to LocalDateTime at start of day.
     * Used to match requests made to Forecast API in WireMockServer Stubs
     * because WireMockServer requires the request path to be exact.
     *
     * @param date LocalDate
     * @return String
     */
    private fun getPathParamDate(date: LocalDate): String {
        val timeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
        return timeFormatter.format(
            OffsetDateTime.from(date.atStartOfDay().atOffset(ZoneOffset.ofHours(0)))
        )
    }

    override fun stop() {
        wireMockServer.stop()
    }
}