package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.home.model.HomeLessonItem

@Composable
fun HomePageStateContent(
    state: HomeState,
    lessonListState: LazyListState,
    onLessonClick: (CourseLesson) -> Unit,
) {
    when (state) {
        is HomeState.Loading -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        is HomeState.Success -> {
            val lessons = state.lessons

            LazyColumn(
                state = lessonListState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(lessons) { lessonItem ->
                    LessonItem(
                        lessonItem = lessonItem,
                        onClick = {
                            when (lessonItem.progress) {
                                HomeLessonItem.Progress.Completed,
                                HomeLessonItem.Progress.Current -> {
                                    onLessonClick(lessonItem.lesson)
                                }

                                HomeLessonItem.Progress.Locked -> {
                                    // Do nothing - this lesson is locked
                                }
                            }
                        },
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            }
        }

        is HomeState.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Error loading course",
                    color = Color.Black,
                    fontSize = 20.sp,
                )
                Text(
                    text = state.message,
                    color = Color.Black,
                    fontSize = 16.sp,
                )
            }
        }
    }
}