package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for SeveraTimeEntryResponse
 */
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
class SeveraTimeEntryResponse {
    var pageContents: List<SeveraTimeEntry>? = null
    var pageSize: Int = 0
    var totalObjectCount: Int = 0
}