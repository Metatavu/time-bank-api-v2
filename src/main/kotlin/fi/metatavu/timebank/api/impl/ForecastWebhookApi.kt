package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.TimeEntryController
import fi.metatavu.timebank.model.ForecastWebhookEvent
import fi.metatavu.timebank.spec.ForecastWebhookApi
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
import org.slf4j.Logger

/**
 * API implementation for ForecastWebhook API
 */
@RequestScoped
@OptIn(ExperimentalCoroutinesApi::class)
class ForecastWebhookApi: ForecastWebhookApi, AbstractApi() {

    @Inject
    lateinit var timeEntryController: TimeEntryController

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var vertx: Vertx

    override fun forecastWebhook(forecastWebhookKey: String, forecastWebhookEvent: ForecastWebhookEvent): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            logger.info("Forecast Webhook ${forecastWebhookEvent.event} event received")
            if (!checkWebhookKey(forecastWebhookKey)) return@async createUnauthorized(message = "Invalid key!")

            if (forecastWebhookEvent.event == "time_registration_deleted") {
                timeEntryController.deleteEntry(forecastId = forecastWebhookEvent.`object`!!.id)
            }
        return@async createNoContent()
    }.asUni()
}
