package fi.metatavu.timebank.api.forecast.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Class for ForecastTaskResponse
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class ForecastTaskResponse {
    var pageContents: List<ForecastTask>? = null
    var pageSize: Int = 0
    var totalObjectCount: Int = 0
}