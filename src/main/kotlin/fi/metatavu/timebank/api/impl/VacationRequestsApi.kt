package fi.metatavu.timebank.api.impl

import fi.metatavu.timebank.api.controllers.VacationRequestController
import fi.metatavu.timebank.api.impl.translate.VacationRequestTranslator
import fi.metatavu.timebank.model.VacationRequest
import fi.metatavu.timebank.spec.VacationRequestsApi
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
import java.time.LocalDate
import java.util.*

/**
 * API implementation for VacationRequests API
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RequestScoped
class VacationRequestsApi: VacationRequestsApi, AbstractApi() {

    @Inject
    lateinit var vacationRequestController: VacationRequestController

    @Inject
    lateinit var vacationRequestTranslator: VacationRequestTranslator

    @Inject
    lateinit var vertx: Vertx

    override fun createVacationRequest(vacationRequest: VacationRequest): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async{
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")

        val newVacationRequest = vacationRequestController.createVacationRequest(
            vacationRequest = vacationRequest,
            creatorsId = userId
        )

        return@async createCreated(entity = vacationRequestTranslator.translate(newVacationRequest))
    }.asUni()

    override fun listVacationRequests(personId: UUID?, before: LocalDate?, after: LocalDate?): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        loggedUserId ?: return@async createUnauthorized("No logged in user!")

        val vacationRequests = vacationRequestController.listVacationRequests(
            personId = personId,
            before = before,
            after = after
        )

        return@async createOk(entity = vacationRequestTranslator.translate(vacationRequests))
    }.asUni()

    override fun findVacationRequest(id: UUID):  Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
            loggedUserId ?: return@async createUnauthorized("Invalid token!")
        val foundVacationRequest = vacationRequestController.findVacationRequest(id) ?: return@async createNotFound("Vacation Request not found")
        return@async createOk(foundVacationRequest)
    }.asUni()

    override fun updateVacationRequest(id: UUID, vacationRequest: VacationRequest): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async {
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")
        val existingVacationRequest = vacationRequestController.findVacationRequest(id) ?: return@async createNotFound("Vacation request not found")

        if (existingVacationRequest.personId != userId) return@async createForbidden("You can only edit your own requests")

        val updatedVacationRequest = vacationRequestController.updateVacationRequest(
            existingVacationRequest = existingVacationRequest,
            vacationRequest = vacationRequest,
        )

        return@async createOk(entity = vacationRequestTranslator.translate(updatedVacationRequest))
    }.asUni()

    override fun deleteVacationRequest(id: UUID): Uni<Response> =
        CoroutineScope(vertx.dispatcher()).async() {
        val userId = loggedUserId ?: return@async createUnauthorized("Invalid token!")
        val existingVacationRequest = vacationRequestController.findVacationRequest(id) ?: return@async createNotFound("Vacation request not found")

        if (existingVacationRequest.personId != userId) return@async createForbidden("You can only delete your own requests")

        vacationRequestController.deleteVacationRequest(id)

        return@async createNoContent()
    }.asUni()
}