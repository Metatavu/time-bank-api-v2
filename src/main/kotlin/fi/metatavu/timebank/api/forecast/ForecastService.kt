package fi.metatavu.timebank.api.forecast

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.metatavu.timebank.api.forecast.models.ForecastHoliday
import fi.metatavu.timebank.api.forecast.models.ForecastPerson
import fi.metatavu.timebank.api.forecast.models.ForecastTaskResponse
import fi.metatavu.timebank.api.forecast.models.ForecastTimeEntryResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import java.time.LocalDate
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject

/**
 * Service for accessing Forecast API
 */
@RequestScoped
class ForecastService {

    @ConfigProperty(name = "forecast.base.url")
    lateinit var forecastBaseUrl: String

    @ConfigProperty(name = "forecast.api.key")
    lateinit var forecastApiKey: String

    @Inject
    lateinit var logger: Logger

    /**
     * Sends get request to Forecast API
     *
     * @param path path for the request
     * @return Response from the request
     */
    private fun doRequest(path: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url("${forecastBaseUrl}${path}")
                .addHeader("X-FORECAST-API-KEY", forecastApiKey)
                .build()
            val response = client.newCall(request).execute()
            when (response.code()) {
                200 -> response.body()?.string()
                else -> throw Error("Couldn't reach Forecast API.")
            }
        } catch (e: Error) {
            logger.error("Error when executing get request: ${e.localizedMessage}")
            throw Error(e.localizedMessage)
        }
    }

    /**
     * Gets persons from Forecast
     *
     * @return List of ForecastPersons
     */
    fun getPersons(): List<ForecastPerson> {
        return jacksonObjectMapper().readValue(
            doRequest("/v2/persons"),
            Array<ForecastPerson>::class.java
        ).toList()
    }

    /**
     * Gets time registrations from Forecast
     *
     * @param date after in YYYY-MM-DD LocalDate
     * @param pageNumber page of paginated response to request
     * @return ForecastTimeEntryResponse
     */
    fun getTimeEntries(after: LocalDate?, pageNumber: Int): ForecastTimeEntryResponse {
        val pathSections = mutableListOf<String>()
        pathSections.add("/v4/time_registrations")
        if (after != null) {
            pathSections.add("/updated_after/${after.toString().replace("-", "")}T000000")
        }
        pathSections.add("?pageSize=1000&pageNumber=$pageNumber")
        return jacksonObjectMapper().readValue(
            doRequest(pathSections.joinToString("")),
            ForecastTimeEntryResponse::class.java
        )
    }

    /**
     * Gets holiday times from Forecast
     *
     * @return List of ForecastHolidays
     */
    fun getHolidays(): List<ForecastHoliday> {
        return jacksonObjectMapper().readValue(
            doRequest("/v1/holiday_calendar_entries"),
            Array<ForecastHoliday>::class.java
        ).toList()
    }

    /**
     * Gets tasks from Forecast
     *
     * @param pageNumber page of paginated response to request
     * @return ForecastTaskResponse
     */
    fun getTasks(pageNumber: Int): ForecastTaskResponse {
        return jacksonObjectMapper().readValue(
            doRequest("/v4/tasks?pageSize=1000&pageNumber=$pageNumber"),
            ForecastTaskResponse::class.java
        )
    }
}