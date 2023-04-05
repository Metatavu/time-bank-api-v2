package fi.metatavu.timebank.api.test.functional.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.logging.LoggerProducer
import fi.metatavu.timebank.api.test.functional.TestBuilder
import fi.metatavu.timebank.api.test.functional.data.TestDateUtils
import io.quarkus.test.common.DevServicesContext
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import okhttp3.*
import org.eclipse.microprofile.config.ConfigProvider
import org.slf4j.LoggerFactory

/**
 * Abstract Test class
 */
abstract class AbstractTest {

    val daysBetweenMonth = ChronoUnit.DAYS.between(TestDateUtils.getThirtyDaysAgo(), LocalDate.now().plusDays(1))
    val daysBetweenTwoMonths = ChronoUnit.DAYS.between(TestDateUtils.getSixtyDaysAgo(), LocalDate.now().plusDays(1))
    val daysBetweenYear = ChronoUnit.DAYS.between(TestDateUtils.getSixtyDaysAgo(), LocalDate.now().plusDays(1))
    val forecastKey = "forecastKey"

    private var devServicesContext: DevServicesContext? = null

    private var logger = LoggerFactory.getLogger(LoggerProducer::class.java)

    private var forecastBaseUrl = "http://localhost:8082"

    data class ReqBody(val state: String)

    /**
     * Creates new test builder
     *
     * @return new test builder
     */
    protected fun createTestBuilder(): TestBuilder {
        return TestBuilder(getConfig())
    }

    /**
     * Returns config for tests.
     *
     * If tests are running in native mode, method returns config from devServicesContext and
     * when tests are running in JVM mode method returns config from the Quarkus config
     *
     * @return config for tests
     */
    private fun getConfig(): Map<String, String> {
        return getDevServiceConfig() ?: getQuarkusConfig()
    }

    /**
     * Returns test config from dev services
     *
     * @return test config from dev services
     */
    private fun getDevServiceConfig(): Map<String, String>? {
        return devServicesContext?.devServicesProperties()
    }

    /**
     * Returns test config from Quarkus
     *
     * @return test config from Quarkus
     */
    private fun getQuarkusConfig(): Map<String, String> {
        val config = ConfigProvider.getConfig()
        return config.propertyNames.associateWith { config.getConfigValue(it).rawValue }
    }

    /**
     * Resets Wiremock scenario states
     */
    fun resetScenarios() {
        try {
            OkHttpClient()
                .newCall(
                    Request.Builder()
                    .url("${forecastBaseUrl}/__admin/scenarios/reset")
                    .post(RequestBody.create(null, ""))
                    .build()
                ).execute()
                .close()
        } catch (e: Error) {
            logger.error("Un-expected error happened while resetting Wiremock Scenarios: ${e.localizedMessage}")
        }
    }

    /**
     * Sets specific Wiremock scenario to specific state
     */
    fun setScenario(scenario: String, state: String) {
        try {
            OkHttpClient()
                .newCall(
                    Request.Builder()
                        .url("${forecastBaseUrl}/__admin/scenarios/$scenario/state")
                        .put(RequestBody.create(
                            MediaType.parse("application/json"),
                            jacksonObjectMapper().writeValueAsString(ReqBody(state = state)
                        )
                        ))
                        .build()
                ).execute()
                .close()
        } catch (e: Error) {
            logger.error("Un-expected error happened while setting Wiremock Scenarios: ${e.localizedMessage}")
        }
    }

    companion object {
        const val PERSONS_SCENARIO = "personsScenario"
        const val HOLIDAYS_SCENARIO = "holidaysScenario"
        const val TIMES_SCENARIO = "timesScenario"
        const val TASKS_SCENARIO = "tasksScenario"
        const val ERROR_STATE = "errorState"
        const val UPDATE_STATE_ONE = "updateStateOne"
        const val UPDATE_STATE_TWO = "updateStateTwo"
        const val YEAR_STATE = "yearState"
        const val DELETE_STATE = "delete"
    }
}