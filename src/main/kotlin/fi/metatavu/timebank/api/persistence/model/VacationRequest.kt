package fi.metatavu.timebank.api.persistence.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * Vacation JPA entity
 */
@Entity
class VacationRequest {

    @Id
    @Column
    lateinit var id: UUID

    @Column
    var person: Int? = null

    @Column
    var startDate: LocalDate? = null

    @Column
    var endDate: LocalDate? = null

    @Column
    var days: Int? = null

    @Column
    var type: String? = null

    @Column
    var message: String? = null

    @Column
    var projectManagerStatus: String? = null

    @Column
    var hrManagerStatus: String? = null

    @Column
    var createdAt: OffsetDateTime? = null

    @Column
    var updatedAt: OffsetDateTime? = null
}