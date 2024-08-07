package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for incoming time entry data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)

class SeveraTimeEntry {
    var id: Int? = null
    var person: Int = 0
    var task: Int? = null
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