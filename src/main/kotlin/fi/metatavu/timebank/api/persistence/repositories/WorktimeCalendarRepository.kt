package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.WorktimeCalendar
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate
import java.util.*

/**
 * Manages WorktimeCalendar JPA entity
 */
@ApplicationScoped
class WorktimeCalendarRepository: PanacheRepositoryBase<WorktimeCalendar, UUID> {

    /**
     * Gets all WorktimeCalendars for given Person
     *
     * @param personId
     * @return List of WorktimeCalendars
     */
    fun getAllWorkTimeCalendarsByPerson(personId: Int): Uni<MutableList<WorktimeCalendar>>? {
        return find("personId = ?1", personId).list()
    }

    /**
     * Updates persisted WorktimeCalendar
     *
     * @param id id
     * @param calendarEnd calendarEnd
     */
    fun updateWorktimeCalendar(id: UUID, calendarEnd: LocalDate) {
        Panache.withTransaction {
            update("calendarEnd = ?1 WHERE id = ?2", calendarEnd, id)
        }.await().indefinitely();
    }

    /**
     * Persists new WorktimeCalendar
     *
     * @param worktimeCalendar WorktimeCalendar
     */
    fun persistWorktimeCalendar(worktimeCalendar: WorktimeCalendar){
        Panache.withTransaction {
            persist(worktimeCalendar)
        }.await().indefinitely()
    }
}