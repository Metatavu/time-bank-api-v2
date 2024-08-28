package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for workContract data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class SeveraWorkContract {
    @JsonProperty("startDate")
    var startDate: String = ""
    @JsonProperty("endDate")
    var endDate: String? = null
}