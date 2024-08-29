package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for User data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class SeveraUser {
    var guid: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var email: String = ""
    @JsonProperty("workContract")
    var severaWorkContract: SeveraWorkContract? = null
    var isActive: Boolean = false
}