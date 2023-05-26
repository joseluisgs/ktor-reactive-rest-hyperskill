package joseluisgs.dev.services.storage


import io.ktor.server.config.*
import joseluisgs.dev.exceptions.StorageException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

class StorageServiceImpl(
    private val storageConfig: ApplicationConfig = ApplicationConfig("application.conf")
) : StorageService {

    private val uploadDir by lazy {
        storageConfig.propertyOrNull("upload.dir")?.getString() ?: "uploads"
    }

    init {
        logger.debug { " Starting Storage Service in $uploadDir" }
        initStorageDirectory()
    }

    private fun initStorageDirectory() {
        // Create upload directory if not exists (or ignore if exists)
        // and clean if dev
        Files.createDirectories(Path.of(uploadDir))
        if (storageConfig.propertyOrNull("ktor.environment")?.getString() == "dev") {
            File(uploadDir).listFiles()?.forEach { it.delete() }
        }
    }

    override suspend fun saveFile(fileName: String, fileUrl: String, fileBytes: ByteArray): Map<String, String> =
        withContext(Dispatchers.IO) {
            logger.debug { "Saving file in: $fileName" }
            try {
                File("${uploadDir}/$fileName").writeBytes(fileBytes)
                return@withContext mapOf(
                    "fileName" to fileName,
                    "createdAt" to LocalDateTime.now().toString(),
                    "size" to fileBytes.size.toString(),
                    "url" to fileUrl,
                )
            } catch (e: Exception) {
                throw StorageException.FileNotFound("No: ${e.message}")
            }
        }

    override suspend fun getFile(fileName: String): File = withContext(Dispatchers.IO) {
        logger.debug { "Get file: $fileName" }
        val file = File("${uploadDir}/$fileName")
        if (!file.exists()) {
            throw StorageException.FileNotFound("File not found: $fileName")
        } else {
            return@withContext file
        }
    }

    override suspend fun deleteFile(fileName: String): Unit = withContext(Dispatchers.IO) {
        logger.debug { "Remove file: $fileName" }
        val file = File("${uploadDir}/$fileName")
        if (!file.exists()) {
            throw StorageException.FileNotFound("File Not Found in storage: $fileName")
        } else {
            file.delete()
        }
    }

}