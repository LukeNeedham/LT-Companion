package com.lukeneedham.languagetransfer.ui.feature.debug

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun DebugPage(
    onBack: () -> Unit,
    openStartup: () -> Unit,
    openCourseDownload: () -> Unit,
    openHome: () -> Unit,
    openLesson: (lesson: CourseLesson) -> Unit,
    viewModel: DebugViewModel = koinViewModel()
) {
    val clipboardManager = LocalClipboardManager.current

    val dummyLesson = CourseLesson(
        name = "Dummy Lesson",
        lessonNumber = 1,
        pausepoints = emptyList(),
        audioFile = File(""),
        totalDuration = 1000,
        colorScheme = ColorScheme(
            colors = listOf(0xFFFFFFFF, 0xFFFFFF00)
        ),
    )

    SystemBarsColor(useDarkIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable {
                        onBack()
                    }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(30.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Debug", fontSize = 25.sp, color = Color.Black)
            }
            Box(
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            SettingsSection(viewModel = viewModel)

            Spacer(modifier = Modifier.height(30.dp))

            NavigateSection(
                openStartup = openStartup,
                openCourseDownload = openCourseDownload,
                openHome = openHome,
                openLesson = openLesson,
                dummyLesson = dummyLesson,
            )

            Spacer(modifier = Modifier.height(30.dp))

            PausepointModificationSection(viewModel, clipboardManager)

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PausepointModificationSection(
    viewModel: DebugViewModel,
    clipboardManager: ClipboardManager
) {
    Text(text = "Pausepoint Modifications")
    Spacer(modifier = Modifier.height(10.dp))

    val pausepointModificationsText = viewModel.modifiedPausepointsJson
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clipboardManager.setText(AnnotatedString(pausepointModificationsText))
            }
            .background(color = Colors.glassy, shape = RoundedCornerShape(20.dp))
            .horizontalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = pausepointModificationsText,
            fontSize = 10.sp,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun NavigateSection(
    openStartup: () -> Unit,
    openCourseDownload: () -> Unit,
    openHome: () -> Unit,
    openLesson: (lesson: CourseLesson) -> Unit,
    dummyLesson: CourseLesson,
) {
    @Composable
    fun OpenPageButton(text: String, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(text)
        }
    }

    Text("Open Page:")
    OpenPageButton(onClick = openStartup, text = "Startup")
    OpenPageButton(onClick = openCourseDownload, text = "Course Download")
    OpenPageButton(onClick = openHome, text = "Home")
    OpenPageButton(onClick = { openLesson(dummyLesson) }, text = "Lesson")
}

@Composable
private fun SettingsSection(
    viewModel: DebugViewModel,
) {
    Text("Settings:")

    @Composable
    fun Setting(
        text: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
            )
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }

    val allLessonsCompleted by viewModel.allLessonsCompleted.collectAsState()
    Setting(
        text = "Unlock all lessons",
        checked = allLessonsCompleted,
        onCheckedChange = {
            viewModel.setAllLessonsCompleted(it)
        },
    )

    val showDebugLessonControls by viewModel.showDebugLessonControls.collectAsState()
    Setting(
        text = "Show debug lesson controls",
        checked = showDebugLessonControls,
        onCheckedChange = {
            viewModel.setShowDebugLessonControls(it)
        },
    )

    val autoPause by viewModel.shouldAutoPause.collectAsState()
    Setting(
        text = "Auto-pause",
        checked = autoPause,
        onCheckedChange = {
            viewModel.setShouldAutoPause(it)
        },
    )
}