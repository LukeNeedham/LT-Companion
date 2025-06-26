package com.lukeneedham.languagetransfer.data.network

import android.content.Context
import com.lukeneedham.languagetransfer.util.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class FileDownloader(private val context: Context) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation)
    }

    /**
     * Downloads a file from the specified URL and saves it to internal storage
     * If the file is a zip file, it will be extracted to the specified output path
     *
     * @param url The URL of the file to download
     * @param outputDir The directory where the file or extracted contents will be saved
     * @param onProgress Callback to report download progress (0.0 to 1.0)
     * @return The downloaded file or directory containing extracted files if it's a zip
     */
    suspend fun downloadFile(
        url: String,
        outputDir: File,
        onProgress: (Float) -> Unit,
    ): AppResult<File> {
        /** Arbitrary fraction as a heuristic for how much of the loading time the download takes */
        val progressFractionDownload = 0.8f
        val progressFractionUnzip = 1 - progressFractionDownload

        fun onDownloadProgress(downloadProgress: Float) {
            val totalProgress = downloadProgress * progressFractionDownload
            onProgress(totalProgress)
        }

        fun onUnzipProgress(unzipProgress: Float) {
            val totalProgress =
                progressFractionDownload + (unzipProgress * progressFractionUnzip)
            onProgress(totalProgress)
        }

        return withContext(Dispatchers.IO) {
            try {
                // Report initial progress
                onDownloadProgress(0f)

                // Create output directory if it doesn't exist
                if (!outputDir.exists()) {
                    outputDir.mkdirs()
                }

                // Create a temporary file for the download
                val tempFile = File(context.cacheDir, "temp_download.zip")

                // Prepare the GET request
                val httpRequest = client.prepareGet(url) {
                    onDownload { bytesSentTotal, contentLength ->
                        val progress = (bytesSentTotal.toFloat() / contentLength.toFloat())
                        onDownloadProgress(progress)
                    }
                }

                // Execute the request and track progress
                val downloadResult = httpRequest.execute { response ->
                    // Check response status
                    if (response.status.value !in 200..299) {
                        return@execute AppResult.Failure<File>("Unexpected response code: ${response.status.value}")
                    }

                    // Write the file with progress tracking
                    FileOutputStream(tempFile).use { outputStream ->
                        val channel = response.bodyAsChannel()
                        var bytesRead = 0L

                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var bytes = channel.readAvailable(buffer, 0, buffer.size)
                        while (bytes >= 0) {
                            outputStream.write(buffer, 0, bytes)
                            bytesRead += bytes.toLong()
                            bytes = channel.readAvailable(buffer, 0, buffer.size)
                        }
                    }

                    // Check if the file is a zip file by its extension
                    if (!url.endsWith(".zip", ignoreCase = true)) {
                        return@execute AppResult.Failure<File>("Downloaded file is not a zip: $url")
                    }

                    // Report download complete, unzipping starting
                    onUnzipProgress(0f)

                    // Unzip the file
                    unzipFile(tempFile, outputDir)

                    // Delete the temporary zip file
                    tempFile.delete()

                    // Report complete
                    onUnzipProgress(1f)

                    AppResult.Success(outputDir)
                }

                return@withContext downloadResult
            } catch (e: Exception) {
                return@withContext AppResult.Failure(e)
            }
        }
    }

    /**
     * Extracts the contents of a zip file to the specified directory
     *
     * @param zipFile The zip file to extract
     * @param destDirectory The directory where the contents will be extracted
     */
    private fun unzipFile(zipFile: File, destDirectory: File) {
        if (!destDirectory.exists()) {
            destDirectory.mkdirs()
        }

        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zipIn ->
            var entry = zipIn.nextEntry

            // Buffer for reading and writing data to file
            val buffer = ByteArray(8192)

            while (entry != null) {
                val filePath = File(destDirectory, entry.name)

                // Create parent directories if they don't exist
                if (!filePath.parentFile!!.exists()) {
                    filePath.parentFile!!.mkdirs()
                }

                // If the entry is a directory, create it and move to next entry
                if (entry.isDirectory) {
                    filePath.mkdirs()
                } else {
                    // Extract the file
                    BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
                        var len: Int
                        while (zipIn.read(buffer).also { len = it } > 0) {
                            bos.write(buffer, 0, len)
                        }
                    }
                }

                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    }
}
