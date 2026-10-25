package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import org.koin.androidx.compose.koinViewModel

/**
 * Home page where users can access the downloaded courses.
 *
 * @param onLessonClick Callback to be invoked when a lesson is clicked
 * @param onSettingsClick Callback to be invoked when the Settings button is clicked
 */
@Composable
fun HomePage(
    onLessonClick: (CourseLesson) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val lessonListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        // Auto scroll to current item
        viewModel.scrollToCurrentLessonFlow.collect {
            lessonListState.animateScrollToItem(it)
        }
    }

    HomePageContent(
        state = viewModel.uiState,
        lessonListState = lessonListState,
        onLessonClick = onLessonClick,
        onSettingsClick = onSettingsClick,
    )
}
