package fi.metatavu.timebank.api.test.functional.resources

import io.quarkus.test.junit.QuarkusTestProfile

/**
 * Local test profile
 */
class LocalTestProfile: QuarkusTestProfile {

    override fun getConfigOverrides(): Map<String, String> {
        return mapOf(
            "keycloak.admin.username" to "admin",
            "keycloak.admin.password" to "admin",
            "keycloak.client.id" to "admin-cli",
            "keycloak.client.secret" to "secret",
            "keycloak.realm" to "timebank",
            "forecast.webhook.key" to "forecastKey"
        )
    }

}