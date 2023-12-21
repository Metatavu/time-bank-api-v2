package fi.metatavu.timebank.api.impl

import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*

/**
 * Abstract base class for all API services
 *
 * @author Jari Nykänen
 */
@RequestScoped
abstract class AbstractApi {

    @ConfigProperty(name = "forecast.webhook.key")
    lateinit var forecastWebhookKey: String

    @Inject
    lateinit var jsonWebToken: JsonWebToken

    @Inject
    lateinit var identity: SecurityIdentity

    /**
     * Returns logged user id
     *
     * @return logged user id
     */
    protected val loggedUserId: UUID?
        get() {
            if (jsonWebToken.subject != null) {
                return UUID.fromString(jsonWebToken.subject)
            }
            return null
        }

    /**
     * Checks if user is admin
     *
     * @return if user is admin
     */
    protected fun isAdmin(): Boolean {
        return identity.hasRole("admin")
    }

    /**
     * Checks if key is correct
     *
     * @return true if key is correct else false
     */
    protected fun checkWebhookKey(key: String): Boolean {
        if (key == forecastWebhookKey) return true

        return false
    }

    /**
     * Constructs ok response with total count header
     *
     * @param entity payload
     * @return response
     */
    protected fun createOk(entity: Any?, count: Long): Response {
        return Response
            .status(Response.Status.OK)
            .entity(entity)
            .header("X-Total-Count", count.toString())
            .header("Access-Control-Expose-Headers", "X-Total-Count")
            .build()
    }

    /**
     * Constructs ok response
     *
     * @param entity payload
     * @return response
     */
    protected fun createOk(entity: Any?): Response {
        return Response
            .status(Response.Status.OK)
            .entity(entity)
            .build()
    }

    /**
     * Constructs ok response
     *
     * @return response
     */
    protected fun createOk(): Response {
        return Response
            .status(Response.Status.OK)
            .build()
    }

    /**
     * Constructs created response
     *
     * @param entity created entity
     * @return response
     */
    protected fun createCreated(entity: Any): Response {
        return Response
            .status(Response.Status.CREATED)
            .entity(entity)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @param entity payload
     * @return response
     */
    protected fun createAccepted(entity: Any?): Response {
        return Response
            .status(Response.Status.ACCEPTED)
            .entity(entity)
            .build()
    }

    /**
     * Constructs no content response
     *
     * @return response
     */
    protected fun createNoContent(): Response {
        return Response
            .status(Response.Status.NO_CONTENT)
            .build()
    }

    /**
     * Constructs bad request response
     *
     * @param message message
     * @return response
     */
    protected fun createBadRequest(message: String): Response {
        return createError(Response.Status.BAD_REQUEST, message)
    }

    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createNotFound(message: String): Response {
        return createError(Response.Status.NOT_FOUND, message)
    }

    /**
     * Constructs not found response
     *
     * @return response
     */
    protected fun createNotFound(): Response {
        return Response
            .status(Response.Status.NOT_FOUND)
            .build()
    }

    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createConflict(message: String): Response {
        return createError(Response.Status.CONFLICT, message)
    }

    /**
     * Constructs not implemented response
     *
     * @param message message
     * @return response
     */
    protected fun createNotImplemented(message: String): Response {
        return createError(Response.Status.NOT_IMPLEMENTED, message)
    }

    /**
     * Constructs internal server error response
     *
     * @param message message
     * @return response
     */
    protected fun createInternalServerError(message: String): Response {
        return createError(Response.Status.INTERNAL_SERVER_ERROR, message)
    }

    /**
     * Constructs forbidden response
     *
     * @param message message
     * @return response
     */
    protected fun createForbidden(message: String): Response {
        return createError(Response.Status.FORBIDDEN, message)
    }

    /**
     * Constructs unauthorized response
     *
     * @param message message
     * @return response
     */
    protected fun createUnauthorized(message: String): Response {
        return createError(Response.Status.UNAUTHORIZED, message)
    }

    /**
     * Constructs an error response
     *
     * @param status status code
     * @param message message
     *
     * @return error response
     */
    private fun createError(status: Response.Status, message: String): Response {
        val entity = fi.metatavu.timebank.model.Error(
            message = message,
            code = status.statusCode
        )

        return Response
            .status(status)
            .entity(entity)
            .build()
    }
}