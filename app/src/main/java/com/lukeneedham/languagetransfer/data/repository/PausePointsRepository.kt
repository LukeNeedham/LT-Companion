package com.lukeneedham.languagetransfer.data.repository

import android.content.Context
import com.lukeneedham.languagetransfer.domain.model.LanguagePausePoints
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Repository for accessing pause points data from JSON files stored in the assets directory.
 */
class PausePointsRepository(private val context: Context) {

    /**
     * Reads pause points for a specific language from a JSON file in the assets directory.
     *
     * @param language The language to get pause points for (e.g., "spanish")
     * @return AppResult containing the pause points map if successful, or an error if not
     */
    fun getPausePoints(language: String): AppResult<LanguagePausePoints> {
        return try {
            val jsonString = readJsonFromAssets(language)
            val pausePoints = parseJsonToPausePoints(jsonString)
            AppResult.Success(pausePoints)
        } catch (e: IOException) {
            AppResult.Failure("Failed to read pause points file: ${e.message}")
        } catch (e: Exception) {
            AppResult.Failure("Failed to parse pause points: ${e.message}")
        }
    }

    /**
     * Reads a JSON file from the assets directory.
     *
     * @param language The language to read the JSON file for
     * @return The JSON file content as a string
     * @throws IOException If the file cannot be read
     */
    private fun readJsonFromAssets(language: String): String {
        val fileName = "pausepoints/$language.json"
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    /**
     * Parses a JSON string into a LanguagePausePoints object, converting string timestamps to milliseconds.
     *
     * @param jsonString The JSON string to parse
     * @return A LanguagePausePoints object with timestamps converted to milliseconds
     */
    private fun parseJsonToPausePoints(jsonString: String): LanguagePausePoints {
        val map = Json.decodeFromString<Map<String, List<Millis>>>(jsonString)
        return LanguagePausePoints(map)
    }
}
