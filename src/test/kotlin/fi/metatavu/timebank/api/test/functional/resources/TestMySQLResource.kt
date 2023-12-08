package fi.metatavu.timebank.api.test.functional.resources

import io.quarkus.test.common.DevServicesContext
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.MySQLContainer
import jakarta.enterprise.context.ApplicationScoped

internal class SpecifiedMySQLContainer(image: String): MySQLContainer<SpecifiedMySQLContainer>(image)

/**
 * Quarkus test resource for providing MySQL database
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class TestMySQLResource: QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    private val db: MySQLContainer<*> = SpecifiedMySQLContainer("mysql:latest")
        .withDatabaseName(DATABASE)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withCommand(
            "--lower_case_table_names=1"
        )

    private var containerNetworkId: String? = null

    override fun setIntegrationTestContext(context: DevServicesContext?) {
        containerNetworkId = context?.containerNetworkId().toString()
    }

    override fun start(): Map<String, String> {
        db.start()

        if (containerNetworkId.isNullOrEmpty()) db::withNetworkMode

        var jdbcUrl = db.jdbcUrl

        if (containerNetworkId.isNullOrEmpty()) {
            jdbcUrl = getContainerJdbcUrl(jdbcUrl)
        }

        val config: MutableMap<String, String> = HashMap()
        config["quarkus.datasource.username"] = USERNAME
        config["quarkus.datasource.password"] = PASSWORD
        config["quarkus.datasource.jdbc.url"] = jdbcUrl
        config["quarkus.datasource.reactive.url"] = jdbcUrl.replace("jdbc:", "")

        return config
    }

    override fun stop() {
        db.stop()
    }

    /**
     * Changes DB host in JdbcUrl to DB host in container network
     *
     * @param jdbcUrl jdbcUrl
     * @return jdbcUrl corrected jdbcUrl
     */
    private fun getContainerJdbcUrl(jdbcUrl: String): String {
        val hostPort = "${db.host}:${db.getMappedPort(MySQLContainer.MYSQL_PORT)}"
        val networkHostPort = "${db.currentContainerInfo.config.hostName}:${MySQLContainer.MYSQL_PORT}"

        return jdbcUrl.replace(hostPort, networkHostPort)
    }

    companion object {
        const val DATABASE = "timebank-test"
        const val USERNAME = "timebank"
        const val PASSWORD = "timebank"
    }
}