package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationRequestController
import fi.metatavu.timebank.api.controllers.VacationRequestStatusController
import fi.metatavu.timebank.api.impl.translate.VacationRequestStatusTranslator
import fi.metatavu.timebank.model.VacationRequestStatus
import fi.metatavu.timebank.spec.VacationRequestStatusApi
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
import java.util.*

/**
 * API implementation for VacationRequestStatus API
 */
@RequestScoped
@OptIn(ExperimentalCoroutinesApi::class)
class VacationRequestStatusApi: VacationRequestStatusApi, AbstractApi() {

    @Inject
    lateinit var vacationRequestController: VacationRequestController

    @Inject
    lateinit var vacationRequestStatusController: VacationRequestStatusController

    @Inject
    lateinit var vacationRequestStatusTranslator: VacationRequestStatusTranslator

    @Inject
    lateinit var vertx: Vertx

    override fun createVacationRequestStatus(id: UUID, vacationRequestStatus: VacationRequestStatus): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")

        if (!isAdmin()) return@async createForbidden("Permission missing")

        val foundVacationRequest = vacationRequestController.findVacationRequest(id) ?: return@async createNotFound("Vacation request not found")
        val newStatus = vacationRequestStatusController.createVacationRequestStatus(foundVacationRequest, vacationRequestStatus, userId)

        return@async createCreated(entity = vacationRequestStatusTranslator.translate(newStatus))
    }.asUni()

    override fun listVacationRequestStatuses(id: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("Invalid token!")

        val statuses = vacationRequestStatusController.listVacationRequestStatus(vacationRequestId = id)

        return@async createOk(entity = vacationRequestStatusTranslator.translate(statuses))
    }.asUni()

    override fun findVacationRequestStatus(id: UUID, statusId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("Invalid token!")

        val foundStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return@async createNotFound("Vacation request status not found")

        return@async createOk(vacationRequestStatusTranslator.translate(foundStatus))
    }.asUni()

    override fun updateVacationRequestStatus(id: UUID, statusId: UUID, vacationRequestStatus: VacationRequestStatus): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")
        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return@async createNotFound("Vacation request status not found")

        if (existingStatus.createdBy != userId && !isAdmin()) return@async createForbidden("Permission missing")

        val updatedStatus = vacationRequestStatusController.updateVacationRequestStatus(
            existingStatus = existingStatus,
            vacationRequestStatus = vacationRequestStatus,
            updaterId = userId
        )

        return@async createOk(entity = vacationRequestStatusTranslator.translate(updatedStatus))

    }.asUni()

    override fun deleteVacationRequestStatus(id: UUID, statusId: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")
        val existingStatus = vacationRequestStatusController.findVacationRequestStatus(statusId) ?: return@async createNotFound("Vacation request status not found")

        if (existingStatus.createdBy != userId) return@async createForbidden("You can only delete your own statuses")

        vacationRequestStatusController.deleteVacationRequestStatus(existingStatus)

        return@async createNoContent()
    }.asUni()
}