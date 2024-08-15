package fi.metatavu.timebank.api.severa.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.quarkus.runtime.annotations.RegisterForReflection

/**
 * Data class for flex time data coming from Severa
 */
@RegisterForReflection
class SeveraFlextime {
    var totalFlextimeBalance: Double = 0.0
    var monthFlextimeBalance: Double = 0.0
}