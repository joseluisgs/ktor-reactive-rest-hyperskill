package joseluisgs.dev.services.storage


import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import joseluisgs.dev.config.AppConfig
import joseluisgs.dev.errors.storage.StorageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.koin.core.annotation.Singleton
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

/**
 * Storage Service to manage our files
 * @property myConfig AppConfig Configuration of our service
 */
@Singleton
class StorageServiceImpl(
    private val myConfig: AppConfig
) : StorageService {

    private val uploadDir by lazy {
        myConfig.applicationConfiguration.propertyOrNull("upload.dir")?.getString() ?: "uploads"
    }

    init {
        logger.debug { " Starting Storage Service in $uploadDir" }
        initStorageDirectory()
    }

    private fun initStorageDirectory() {
        // Create upload directory if not exists (or ignore if exists)
        // and clean if dev
        Files.createDirectories(Path.of(uploadDir))
        if (myConfig.applicationConfiguration.propertyOrNull("ktor.environment")?.getString() == "dev") {
            logger.debug { "Cleaning storage directory in $uploadDir" }
            File(uploadDir).listFiles()?.forEach { it.delete() }
        }
    }

    override suspend fun saveFile(
        fileName: String,
        fileUrl: String,
        fileBytes: ByteArray
    ): Result<Map<String, String>, StorageError> =
        withContext(Dispatchers.IO) {
            logger.debug { "Saving file in: $fileName" }
            return@withContext try {
                File("${uploadDir}/$fileName").writeBytes(fileBytes)
                Ok(
                    mapOf(
                        "fileName" to fileName,
                        "createdAt" to LocalDateTime.now().toString(),
                        "size" to fileBytes.size.toString(),
                        "url" to fileUrl,
                    )
                )
            } catch (e: Exception) {
                Err(StorageError.BadRequest("Error saving file: $fileName"))
            }
        }

    override suspend fun getFile(fileName: String): Result<File, StorageError> = withContext(Dispatchers.IO) {
        logger.debug { "Get file: $fileName" }
        return@withContext if (!File("${uploadDir}/$fileName").exists()) {
            Err(StorageError.NotFound("File Not Found in storage: $fileName"))
        } else {
            Ok(File("${uploadDir}/$fileName"))
        }
    }

    override suspend fun deleteFile(fileName: String): Result<String, StorageError> = withContext(Dispatchers.IO) {
        logger.debug { "Remove file: $fileName" }
        Files.deleteIfExists(Path.of("${uploadDir}/$fileName"))
        Ok(fileName)
    }

}