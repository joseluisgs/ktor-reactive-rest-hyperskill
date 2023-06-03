package joseluisgs.dev.services.database

import io.ktor.server.config.*
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactoryOptions
import joseluisgs.dev.data.racketsDemoData
import joseluisgs.dev.entities.RacketTable
import joseluisgs.dev.mappers.toEntity
import joseluisgs.dev.models.Racket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.ufoss.kotysa.H2Tables
import org.ufoss.kotysa.r2dbc.coSqlClient
import org.ufoss.kotysa.tables

private val logger = KotlinLogging.logger {}

/**
 * DataBase Service to connect to our database
 * @param dataBaseConfig Configuration of our database from application.conf
 */
class DataBaseService(
    private val dataBaseConfig: ApplicationConfig = ApplicationConfig("application.conf")
) {

    private val connectionFactory by lazy {
        val options = ConnectionFactoryOptions.builder()
            .option(
                ConnectionFactoryOptions.DRIVER,
                dataBaseConfig.propertyOrNull("database.driver")?.getString() ?: "h2"
            )
            .option(
                ConnectionFactoryOptions.PROTOCOL,
                dataBaseConfig.propertyOrNull("database.protocol")?.getString() ?: "mem"
            )
            .option(
                ConnectionFactoryOptions.USER,
                dataBaseConfig.propertyOrNull("database.user")?.getString() ?: "sa"
            )
            .option(
                ConnectionFactoryOptions.PASSWORD,
                dataBaseConfig.propertyOrNull("database.password")?.getString() ?: ""
            )
            .option(
                ConnectionFactoryOptions.DATABASE,
                dataBaseConfig.propertyOrNull("database.database")?.getString()
                    ?: "r2dbc:h2:mem:///test;DB_CLOSE_DELAY=-1"
            )
            .build()
        ConnectionFactories.get(options)
    }

    private val initDatabaseData by lazy {
        dataBaseConfig.propertyOrNull("database.initDatabaseData")?.getString()?.toBoolean() ?: false
    }

    // Our client
    val client = connectionFactory.coSqlClient(getTables())

    init {
        logger.debug { "Init DataBaseService" }
        initDatabase()
    }

    // Our tables
    private fun getTables(): H2Tables {
        // Return tables
        return tables().h2(RacketTable)
    }

    private fun initDatabase() = runBlocking {
        logger.debug { "Init DatabaseService" }
        createTables()
        // Init data
        if (initDatabaseData) {
            initDataBaseDataDemo()
        }
    }

    // demo data
    suspend fun initDataBaseDataDemo() {
        clearDataBaseData()
        initDataBaseData()
    }

    // Create tables if not exists
    private suspend fun createTables() = withContext(Dispatchers.IO) {
        logger.debug { "Creating the tables..." }
        launch {
            client createTableIfNotExists RacketTable
        }
    }

    // Clear all data
    private suspend fun clearDataBaseData() = withContext(Dispatchers.IO) {
        logger.debug { "Deleting data..." }
        launch {
            client deleteAllFrom RacketTable
        }
    }

    // Init data
    private suspend fun initDataBaseData() = withContext(Dispatchers.IO) {
        logger.debug { "Saving rackets demo data..." }
        launch {
            racketsDemoData().forEach {
                client insert it.value.copy(id = Racket.NEW_RACKET).toEntity()
            }
        }
    }
}