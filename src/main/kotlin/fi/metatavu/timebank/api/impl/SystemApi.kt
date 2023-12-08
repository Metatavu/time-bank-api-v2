package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.spec.SystemApi
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Response

/**
 * API implementation for System API
 */
@RequestScoped
class SystemApi: SystemApi, AbstractApi() {
    override suspend fun ping(): Response {
        return createOk("Pong")
    }
}