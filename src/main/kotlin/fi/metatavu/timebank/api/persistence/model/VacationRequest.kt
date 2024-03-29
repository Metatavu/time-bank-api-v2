package fi.metatavu.timebank.api.persistence.model

import fi.metatavu.timebank.model.VacationType
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * VacationRequest JPA entity
 */
@Entity
class VacationRequest(

    @Id
    @Column
    var id: UUID? = null,

    @Column
    var personId: UUID? = null,

    @Column
    var startDate: LocalDate? = null,

    @Column
    var endDate: LocalDate? = null,

    @Column
    var days: Int? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var type: VacationType? = null,

    @Column
    var message: String? = null,

    @Column
    var createdAt: OffsetDateTime? = null,

    @Column
    var updatedAt: OffsetDateTime? = null,
)