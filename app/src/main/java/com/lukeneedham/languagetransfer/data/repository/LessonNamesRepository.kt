package com.lukeneedham.languagetransfer.data.repository

import android.content.Context
import com.lukeneedham.languagetransfer.domain.model.LessonName
import com.lukeneedham.languagetransfer.util.AppResult
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * Repository for accessing lesson names from JSON files stored in the assets directory.
 */
class LessonNamesRepository(private val context: Context) {

    /**
     * Reads lesson names for a specific language from a JSON file in the assets directory.
     *
     * @param language The language to get lesson names for (e.g., "spanish")
     * @return AppResult containing the list of lesson names if successful, or an error if not
     */
    fun getLessonNames(language: String): AppResult<List<LessonName>> {
        return try {
            val fileName = "lessonnames/$language.json"
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val lessonNames = Json.decodeFromString<List<LessonName>>(jsonString)
            AppResult.Success(lessonNames)
        } catch (e: IOException) {
            AppResult.Failure("Failed to read lesson names file: ${e.message}")
        } catch (e: Exception) {
            AppResult.Failure("Failed to parse lesson names: ${e.message}")
        }
    }
}
