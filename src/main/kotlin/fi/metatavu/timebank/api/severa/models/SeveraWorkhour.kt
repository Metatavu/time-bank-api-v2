package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for incoming time entry data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)

class SeveraWorkhour {
    var guid: String? = null
    var user: String? = null
    var project: String? = null
    var eventDate: String = ""
    @JsonProperty("created_by")
    var createdBy: String? = null
    @JsonProperty("updated_by")
    var updatedBy: String? = null
    @JsonProperty("created_at")
    var createdAt: String = ""
    @JsonProperty("updated_at")
    var updatedAt: String = ""
}