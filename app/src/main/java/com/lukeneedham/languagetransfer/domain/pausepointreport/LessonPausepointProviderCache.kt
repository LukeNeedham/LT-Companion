package com.lukeneedham.languagetransfer.domain.pausepointreport

import com.lukeneedham.languagetransfer.data.persistence.prefs.PausepointModificationsDao
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import java.util.concurrent.ConcurrentHashMap

/**
 * Responsible for ensuring that callers get the same [LessonPausepointProvider] instance
 * for a given [CourseLesson]
 */
class LessonPausepointProviderCache(
    private val pausepointModificationsDao: PausepointModificationsDao,
) {
    val cache = ConcurrentHashMap<CourseLesson, LessonPausepointProvider>()

    fun get(lesson: CourseLesson): LessonPausepointProvider {
        val cacheHit = cache[lesson]
        if (cacheHit != null) return cacheHit

        val new = create(lesson)
        cache[lesson] = new
        return new
    }

    private fun create(lesson: CourseLesson) =
        LessonPausepointProvider(lesson, pausepointModificationsDao)
}