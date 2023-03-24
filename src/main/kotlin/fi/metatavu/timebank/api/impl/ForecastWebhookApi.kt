package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.TimeEntryController
import fi.metatavu.timebank.model.ForecastWebhookEvent
import javax.enterprise.context.RequestScoped
import fi.metatavu.timebank.spec.ForecastWebhookApi
import javax.inject.Inject
import javax.ws.rs.core.Response

/**
 * API implementation for TimeEntries API
 */
@RequestScoped
class ForecastWebhookApi: ForecastWebhookApi, AbstractApi() {

    @Inject
    lateinit var timeEntryController: TimeEntryController

        override suspend fun forecastWebhook(forecastWebhookEvent: ForecastWebhookEvent, forecastWebhookKey: String?): Response {
            if (!checkWebhookKey(forecastWebhookKey)) return createUnauthorized(message = "Invalid key!")

            if (forecastWebhookEvent.event == "time_registration_deleted") {
                timeEntryController.deleteEntry(forecastId = forecastWebhookEvent.`object`!!.id)
            }
            return createNoContent()
        }
    }
