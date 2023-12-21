package fi.metatavu.timebank.api.persistence.repositories

import fi.metatavu.timebank.api.persistence.model.TimeEntry
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.coroutines.awaitSuspending
import java.time.LocalDate
import java.util.UUID
import jakarta.enterprise.context.ApplicationScoped

/**
 * Manages TimeEntry JPA entity
 */
@ApplicationScoped
class TimeEntryRepository: AbstractRepository<TimeEntry, UUID>() {

    /**
     * Lists TimeEntries based on given parameters
     *
     * @param personId persons id in Forecast
     * @param before LocalDate to retrieve entries before given date
     * @param after LocalDate to retrieve entries after given date
     * @param vacation filter vacation days
     * @return List of TimeEntries
     */
    fun getEntries(personId: Int?, before: LocalDate?, after: LocalDate?, vacation: Boolean?): List<TimeEntry> {
        val stringBuilder = StringBuilder()
        val parameters = Parameters()

        if (personId != null) {
            stringBuilder.append("person = :personId")
            parameters.and("personId", personId)
        }

        if (before != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date <= :before" else "date <= :before")
            parameters.and("before", before)
        }

        if (after != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and date >= :after" else "date >= :after")
            parameters.and("after", after)
        }

        if (vacation != null) {
            stringBuilder.append(if (stringBuilder.isNotEmpty()) " and isVacation = :vacation" else "isVacation = :vacation")
            parameters.and("vacation", vacation)
        }

        stringBuilder.append(" order by date DESC")

        return listWithParameters(stringBuilder.toString(), parameters)
    }

    /**
     * Persists new TimeEntry
     * Replaces already stored entry if entry is updated
     *
     * @param entry TimeEntry
     * @return true for persisted false for not persisted
     */
    fun persistEntry(entry: TimeEntry): Boolean {
        val existingEntry = find("forecastId", entry.forecastId).list<TimeEntry>().await().indefinitely()
        if (existingEntry.isEmpty()) {
            Panache.withTransaction { persist(entry) }.await().indefinitely()
            return true
        }

        if (entry.updatedAt!! > entry.createdAt!!) {
            return if (existingEntry.first() == entry) {
                false
            } else {
                deleteEntry(entry.forecastId!!)
                Panache.withTransaction { persist(entry) }.await().indefinitely()
                true
            }
        }

        return false
    }

    /**
     * Deletes persisted TimeEntry based on forecastId
     *
     * @param forecastId id of time registration in Forecast
     */
    fun deleteEntry(forecastId: Int) {
        Panache.withTransaction { delete("forecastId", forecastId) }.await().indefinitely()
    }

    /**
     * Deletes persisted TimeEntry based on entryId
     *
     * @param id id of time registration
     */
    fun deleteEntry(id: UUID) {
        Panache.withTransaction { deleteById(id) }.await().indefinitely()
    }
}