package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for workContract data coming from Severa
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class SeveraWorkContract {
    var startDate: String = ""
    var endDate: String? = null
}