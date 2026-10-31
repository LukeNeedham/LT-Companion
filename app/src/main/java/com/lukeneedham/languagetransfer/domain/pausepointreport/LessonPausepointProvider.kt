package com.lukeneedham.languagetransfer.domain.pausepointreport

import com.lukeneedham.common.logger.Logger
import com.lukeneedham.languagetransfer.data.persistence.prefs.PausepointModificationsDao
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonPausepointProvider(
    private val lesson: CourseLesson,
    private val pausepointModificationsDao: PausepointModificationsDao,
) {
    private val lessonName = lesson.audioFileName
    private val reports = mutableListOf<PausepointReport>()

    private val modifiedPausepoints = MutableStateFlow(lesson.pausepoints)
    val pausepoints = modifiedPausepoints.asStateFlow()

    init {
        GlobalScope.launch {
            val modified = pausepointModificationsDao.getModifiedPausepoints(lessonName)
            if (modified != null) {
                modifiedPausepoints.value = modified
            }
        }
    }

    fun report(report: PausepointReport) {
        reports.add(report)
        when (report) {
            is PausepointReport.Add -> {
                update {
                    add(report.at)
                }
            }

            is PausepointReport.TooEarly -> {
                update {
                    val pausepoint = report.pausepoint
                    remove(pausepoint)
                    val newPausepoint = pausepoint + jumpMillis
                    add(newPausepoint)
                }
            }

            is PausepointReport.TooLate -> {
                update {
                    val pausepoint = report.pausepoint
                    remove(pausepoint)
                    val newPausepoint = pausepoint - jumpMillis
                    add(newPausepoint)
                }
            }

            is PausepointReport.Remove -> {
                update {
                    remove(report.pausepoint)
                }
            }
        }
    }

    private fun update(
        block: MutableList<Millis>.() -> Unit,
    ) {
        val newPps = modifiedPausepoints.value.toMutableList()
        newPps.block()
        newPps.sort()
        modifiedPausepoints.value = newPps
        Logger.e(tag = "LessonPausepointReport", message = "New pausepoints: $newPps")
        pausepointModificationsDao.setModifiedPausepoints(lessonName, newPps)
    }

    companion object {
        val jumpMillis = 100
    }
}