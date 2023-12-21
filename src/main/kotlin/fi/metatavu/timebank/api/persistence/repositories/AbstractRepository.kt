package fi.metatavu.timebank.api.persistence.repositories

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters

abstract class AbstractRepository<T, E>: PanacheRepositoryBase<T, E,> {

    fun listWithParameters(queryString: String, parameters: Parameters): List<T>{
        return find(queryString, parameters).list<T>().await().indefinitely()
    }

    fun deleteByIdSuspending(id: E) {
        Panache.withTransaction { deleteById(id) }.await().indefinitely()
    }

    fun deleteSuspending(entity: T) {
        Panache.withTransaction { delete(entity) }.await().indefinitely()
    }

    fun persistSuspending(entity: T): T {
        return Panache.withTransaction { persist(entity) }.await().indefinitely()
    }

    fun findSuspending(id: E): T? {
        return findById(id).await().indefinitely()
    }
}