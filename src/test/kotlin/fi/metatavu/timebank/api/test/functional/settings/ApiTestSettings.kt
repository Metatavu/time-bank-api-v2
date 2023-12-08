package fi.metatavu.timebank.api.test.functional.settings

/**
 * Settings implementation for test builder
 *
 * @author Jari Nykänen
 * @author Antti Leppä
 */
class ApiTestSettings {

    companion object {

        /**
         * Return API service base path
         */
        val apiBasePath: String
        get() = "http://localhost:8081"
    }
}