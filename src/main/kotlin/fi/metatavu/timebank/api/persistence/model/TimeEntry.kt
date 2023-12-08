package fi.metatavu.timebank.api.persistence.model

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

/**
 * TimeEntry JPA entity
 */
@Entity
class TimeEntry {

    @Id
    @Column
    lateinit var id: UUID

    @Column
    var forecastId: Int? = null

    @Column
    var person: Int? = null

    @Column
    var internalTime: Int? = null

    @Column
    var billableProjectTime: Int? = null

    @Column
    var nonBillableProjectTime: Int? = null

    @Column
    var date: LocalDate? = null

    @Column
    var createdAt: OffsetDateTime? = null

    @Column
    var updatedAt: OffsetDateTime? = null

    @Column
    var isVacation: Boolean? = false

    /**
     * Compares object equality ignoring entryId
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        other as TimeEntry
        return forecastId == other.forecastId &&
            person == other.person &&
            internalTime == other.internalTime &&
            billableProjectTime == other.billableProjectTime &&
            nonBillableProjectTime == other.nonBillableProjectTime &&
            date == other.date &&
            createdAt == other.createdAt &&
            updatedAt == other.updatedAt
    }
}