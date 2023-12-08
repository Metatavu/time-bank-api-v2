package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for TimeEntry data coming from Forecast
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastTimeEntry {
    var id: Int? = null
    var person: Int = 0
    var task: Int? = null
    @JsonProperty("non_project_time")
    var nonProjectTime: Int? = null
    @JsonProperty("time_registered")
    var timeRegistered: Int = 0
    var date: String = ""
    @JsonProperty("created_by")
    var createdBy: Int = 0
    @JsonProperty("updated_by")
    var updatedBy: Int = 0
    @JsonProperty("created_at")
    var createdAt: String = ""
    @JsonProperty("updated_at")
    var updatedAt: String = ""
}