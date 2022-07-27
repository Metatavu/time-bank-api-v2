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
     * Gets UserRepresentation from List of UserResources based on given email
     *
     * @param email email
     * @return UserResource
     */
    fun getUserByEmail(email: String): UserRepresentation? {
        val users = getUserResources()?.list()

        return users?.find { it.email == email.lowercase() }
    }

    /**
     * Gets minimumBillableRate attribute for Person
     * If not set will return default value of 50 (%)
     *
     * @param email Persons email
     * @return Int minimumBillableRate
     */
    fun getUsersMinimumBillableRate(email: String): Int {
        val user = getUserByEmail(email)

        return try {
            user!!.attributes["minimumBillableRate"]!!.first()!!.toInt()
        } catch (e: Exception) {
            updateUsersMinimumBillableRate(email, 50)
            50
        }
    }

    /**
     * Updates Persons minimumBillableRate attribute
     *
     * @param email Persons email
     * @param  newMinimumBillableRate Int
     * @return Int minimumBillableRate
     */
    fun updateUsersMinimumBillableRate(email: String, newMinimumBillableRate: Int) {
        val user = getUserByEmail(email) ?: return
        val usersResource = getUserResources()?.get(user.id)

        try {
            user.attributes["minimumBillableRate"] = listOf(newMinimumBillableRate.toString())
            usersResource?.update(user)
        } catch (e: NullPointerException) {
            user.attributes = mapOf("minimumBillableRate" to listOf(newMinimumBillableRate.toString()))
            usersResource?.update(user)
        }
    }

    /**
     * Gets Keycloak UsersResource
     * e.g. list of Keycloak Users
     *
     * @return UsersResource
     */
    private fun getUserResources(): UsersResource? {
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