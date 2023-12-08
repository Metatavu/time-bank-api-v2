package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for Person data coming from Forecast
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastPerson {
    var id: Int = 0
    @JsonProperty("first_name")
    var firstName: String = ""
    @JsonProperty("last_name")
    var lastName: String = ""
    var email: String = ""
    var monday: Int = 0
    var tuesday: Int = 0
    var wednesday: Int = 0
    var thursday: Int = 0
    var friday: Int = 0
    var saturday: Int = 0
    var sunday: Int = 0
    var active: Boolean = false
    var language: String = ""
    @JsonProperty("holiday_calendar_id")
    var holidayCalendarId: Int = 0
    @JsonProperty("start_date")
    var startDate: String = ""
    @JsonProperty("end_date")
    var endDate: String? = null
    @JsonProperty("created_at")
    var createdAt: String = ""
    @JsonProperty("updated_at")
    var updatedAt: String? = null
    @JsonProperty("client_id")
    var clientId: Int? = null
    @JsonProperty("is_system_user")
    var isSystemUser: Boolean = false
    var unspentVacations: Int = 0
    var spentVacations: Int = 0
    var minimumBillableRate: Int = 0
}