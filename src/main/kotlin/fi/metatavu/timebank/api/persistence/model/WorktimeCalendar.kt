package fi.metatavu.timebank.api.persistence.model

import java.time.LocalDate
import java.util.UUID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

/**
 * WorktimeCalendar JPA entity
 */
@Entity
class WorktimeCalendar(

    @Id
    @Column
    var id: UUID? = null,

    @Column
    var personId: Int? = null,

    @Column
    var monday: Int? = null,

    @Column
    var tuesday: Int? = null,

    @Column
    var wednesday: Int? = null,

    @Column
    var thursday: Int? = null,

    @Column
    var friday: Int? = null,

    @Column
    var saturday: Int? = null,

    @Column
    var sunday: Int? = null,

    @Column
    var calendarStart: LocalDate? = null,

    @Column
    var calendarEnd: LocalDate? = null
)