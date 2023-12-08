package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Class for Task data coming from Forecast
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastTask {
    var id: Int = 0
    var title: String = ""
    @JsonProperty("un_billable")
    var unBillable: Boolean = false
}