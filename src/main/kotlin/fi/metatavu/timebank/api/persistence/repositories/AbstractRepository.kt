package fi.metatavu.timebank.api.persistence.repositories

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.coroutines.awaitSuspending

abstract class AbstractRepository<T, E>: PanacheRepositoryBase<T, E,> {

    suspend fun listWithParameters(queryString: String, parameters: Parameters): List<T>{
        return find(queryString, parameters).list<T>().awaitSuspending()
    }

    suspend fun deleteByIdSuspending(id: E) {
        Panache.withTransaction { deleteById(id) }.awaitSuspending()
    }

    suspend fun deleteSuspending(entity: T) {
        Panache.withTransaction { delete(entity) }.awaitSuspending()
    }

    suspend fun persistSuspending(entity: T): T {
        return Panache.withTransaction { persist(entity) }.awaitSuspending()
    }

    suspend fun findSuspending(id: E): T? {
        return findById(id).awaitSuspending()
    }
}