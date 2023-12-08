package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.WorktimeCalendar
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.time.LocalDate
import java.util.*
import jakarta.enterprise.context.ApplicationScoped

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
    suspend fun getAllWorkTimeCalendarsByPerson(personId: Int): List<WorktimeCalendar>? {
        return find("personId = ?1", personId).list<WorktimeCalendar?>().awaitSuspending()
    }

    /**
     * Updates persisted WorktimeCalendar
     *
     * @param id id
     * @param calendarEnd calendarEnd
     */
    suspend fun updateWorktimeCalendar(id: UUID, calendarEnd: LocalDate) {
        Panache.withTransaction {
            update("calendarEnd = ?1 WHERE id = ?2", calendarEnd, id)
        }.awaitSuspending()
    }

    /**
     * Persists new WorktimeCalendar
     *
     * @param worktimeCalendar WorktimeCalendar
     */
    suspend fun persistWorktimeCalendar(worktimeCalendar: WorktimeCalendar){
        Panache.withTransaction {
            persist(worktimeCalendar)
        }.awaitSuspending()
    }
}