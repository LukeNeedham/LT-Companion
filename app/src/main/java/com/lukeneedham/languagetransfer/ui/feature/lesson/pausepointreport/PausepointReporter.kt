package com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport

interface PausepointReporter {
    fun add()

    fun remove()

    fun shiftLater()

    fun shiftEarlier()
}