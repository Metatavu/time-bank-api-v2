package fi.metatavu.timebank.api.keycloak

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.UsersResource
import javax.enterprise.context.ApplicationScoped
import org.keycloak.representations.idm.UserRepresentation

/**
 * Class for Keycloak controller
 */
@ApplicationScoped
class KeycloakController {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    private lateinit var authServerUrl: String

    @ConfigProperty(name = "keycloak.admin.username")
    private lateinit var adminUsername: String

    @ConfigProperty(name = "keycloak.admin.password")
    private lateinit var adminPassword: String

    @ConfigProperty(name = "keycloak.client.id")
    private lateinit var clientId: String

    @ConfigProperty(name = "keycloak.client.secret")
    private lateinit var clientSecret: String

    @ConfigProperty(name = "keycloak.realm")
    private lateinit var realm: String

    /**
     * Gets minimumBillableRate attribute for Person
     * If not set will set and return default value of 75 (%)
     *
     * @param user UserRepresentation
     * @return Int minimumBillableRate
     */
    fun getUsersMinimumBillableRate(user: UserRepresentation): Int {

        return try {
            user.attributes["minimumBillableRate"]!!.first()!!.toInt()
        } catch (e: Exception) {
            updateUsersMinimumBillableRate(user, 75)
            75
        }
    }

    /**
     * Updates Persons minimumBillableRate attribute
     *
     * @param user UserRepresentation
     * @param  newMinimumBillableRate Int
     * @return Int minimumBillableRate
     */
    fun updateUsersMinimumBillableRate(user: UserRepresentation, newMinimumBillableRate: Int) {
        val usersResource = getUsersResource()?.get(user.id)

        try {
            user.attributes["minimumBillableRate"] = listOf(newMinimumBillableRate.toString())
            usersResource?.update(user)
        } catch (e: NullPointerException) {
            user.attributes = mapOf("minimumBillableRate" to listOf(newMinimumBillableRate.toString()))
            usersResource?.update(user)
        }
    }

    /**
     * Finds person by their email
     *
     * @param email String
     * @return UserRepresentation
     */
    fun findUserByEmail(email: String): UserRepresentation? {
        return getKeycloakClient().realm(realm).users().search(
            null,
            null,
            null,
            email,
            null,
            null
        ).firstOrNull()
    }

    /**
     * Gets Keycloak UsersResource
     * e.g. list of Keycloak Users
     *
     * @return UsersResource
     */
    fun getUsersResource(): UsersResource? {
        val keycloakClient = getKeycloakClient()
        val foundRealm = keycloakClient.realm(realm) ?: return null

        return foundRealm.users()
    }

    /**
     * Builds a Keycloak Admin Client
     *
     * @return Keycloak client
     */
    private fun getKeycloakClient(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(authServerUrl.substringBeforeLast("/").substringBeforeLast("/"))
            .realm(realm)
            .clientId(clientId)
            .clientSecret(clientSecret)
            .grantType("client_credentials")
            .username(adminUsername)
            .password(adminPassword)
            .build()
    }
}