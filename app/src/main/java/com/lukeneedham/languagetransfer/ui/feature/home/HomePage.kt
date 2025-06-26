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
 * @param viewModel The view model for this page
 * @param onDebugClick Callback to be invoked when the Debug button is clicked
 */
@Composable
fun HomePage(
    onLessonClick: (CourseLesson) -> Unit,
    onDebugClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val lessonListState = rememberLazyListState()
    val state = viewModel.uiState

    SystemBarsColor(useDarkIcons = true)

    LaunchedEffect(Unit) {
        // Auto scroll to current item
        viewModel.scrollToCurrentLessonFlow.collect {
            lessonListState.animateScrollToItem(it)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.background)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box(
                modifier = Modifier.size(50.dp)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Spanish", fontSize = 25.sp, color = Color.Black)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { onDebugClick() }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_debug),
                    contentDescription = "Debug",
                    modifier = Modifier.height(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            HomePageStateContent(
                state = state,
                lessonListState = lessonListState,
                onLessonClick = onLessonClick
            )
        }
    }
}
