package fi.metatavu.timebank.api.persistence.model

import java.time.LocalDate
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
    var message: String? = null

    @Column
    var person: Int? = null

    @Column
    var days: Int? = null

    @Column
    var startDate: LocalDate? = null

    @Column
    var endDate: LocalDate? = null

    @Column
    var status: Boolean? = null

    /**
     * Compares object equality ignoring entryId
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as VacationRequest
        return message == other.message &&
        person == other.person &&
        days == other.days &&
        startDate == other.startDate &&
        endDate == other.endDate &&
        status == other.status
    }
}