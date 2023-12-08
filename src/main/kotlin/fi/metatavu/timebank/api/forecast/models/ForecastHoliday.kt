package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for holiday data coming from Forecast
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastHoliday {
    var id: Int = 0
    @JsonProperty("holiday_calendar_id")
    var holidayCalendarId: Int = 0
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var name: String = ""
}