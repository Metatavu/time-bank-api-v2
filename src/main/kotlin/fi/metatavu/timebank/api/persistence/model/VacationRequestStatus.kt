package fi.metatavu.timebank.api.persistence.model

import fi.metatavu.timebank.model.VacationRequestStatuses
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

/**
 * VacationRequestStatus JPA entity
 */
@Entity
class VacationRequestStatus(

    @Id
    @Column
    var id: UUID? = null,

    @ManyToOne
    var vacationRequest: VacationRequest? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var status: VacationRequestStatuses? = null,

    @Column
    var message: String? = null,

    @Column
    var createdBy: UUID? = null,

    @Column
    var createdAt: OffsetDateTime? = null,

    @Column
    var updatedBy: UUID? = null,

    @Column
    var updatedAt: OffsetDateTime? = null,
    )