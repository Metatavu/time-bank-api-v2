package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for holiday data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class SeveraHoliday {
    var guid: String = ""
    @JsonProperty("holiday_date")
    var date: String = ""
    @JsonProperty("holiday_name")
    var name: String = ""
    var isPublicHoliday: Boolean = false
}