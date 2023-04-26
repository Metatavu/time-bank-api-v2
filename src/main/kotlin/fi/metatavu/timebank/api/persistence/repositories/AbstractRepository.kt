package fi.metatavu.timebank.api.persistence.repositories

import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.coroutines.awaitSuspending

abstract class AbstractRepository<T, E>: PanacheRepositoryBase<T, E,> {

    protected suspend fun listWithParameters(queryString: String, parameters: Parameters): List<T>{
        return find(queryString, parameters).list<T>().awaitSuspending()
    }

    suspend fun deleteSuspending(id: E) {
        Panache.withTransaction { deleteById(id) }.awaitSuspending()
    }

    suspend fun persistSuspending(entity: T): T {
        return Panache.withTransaction { persist(entity) }.awaitSuspending()
    }

    suspend fun findSuspending(id: E): T {
        return Panache.withTransaction { findById(id)}.awaitSuspending()
    }

    protected suspend fun queryBuilder(strings: Map<String, String>, params: Map<String, Any>, order: String): List<T>{
        val stringBuilder = StringBuilder()
        val parameters = Parameters()
        strings.entries.forEach {
            stringBuilder.append(it.value)
            parameters.and(it.key, params[it.key])
        }

        stringBuilder.append(order)

        return find(stringBuilder.toString(), parameters).list<T>().awaitSuspending()
    }
}