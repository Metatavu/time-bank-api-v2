package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for ForecastTimeEntryResponse
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastTimeEntryResponse {
    var pageContents: List<ForecastTimeEntry>? = null
    var pageSize: Int = 0
    var totalObjectCount: Int = 0
}