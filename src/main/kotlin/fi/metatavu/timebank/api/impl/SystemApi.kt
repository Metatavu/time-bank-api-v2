package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.spec.SystemApi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.asUni
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

/**
 * API implementation for System API
 */
@RequestScoped
@OptIn(ExperimentalCoroutinesApi::class)
class SystemApi: SystemApi, AbstractApi() {

    @Inject
    lateinit var vertx: Vertx

    override fun ping(): Uni<Response> = CoroutineScope(vertx.dispatcher()).async {
        return@async createOk("Pong")
    }.asUni()
}