package joseluisgs.dev.services.storage

import java.io.File

interface StorageService {
    suspend fun saveFile(fileName: String, fileUrl: String, fileBytes: ByteArray): Map<String, String>
    suspend fun getFile(fileName: String): File
    suspend fun deleteFile(fileName: String)
}