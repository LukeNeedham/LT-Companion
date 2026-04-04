package com.lukeneedham.languagetransfer.di

import com.lukeneedham.languagetransfer.data.network.FileDownloader
import com.lukeneedham.languagetransfer.data.network.LanguageDownloadRepository
import com.lukeneedham.languagetransfer.data.persistence.AppDatabase
import com.lukeneedham.languagetransfer.data.persistence.prefs.DebugPreferencesDao
import com.lukeneedham.languagetransfer.data.persistence.prefs.PausepointModificationsDao
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.data.repository.LessonNamesRepository
import com.lukeneedham.languagetransfer.data.repository.PausePointsRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProviderCache
import com.lukeneedham.languagetransfer.ui.feature.debug.DebugViewModel
import com.lukeneedham.languagetransfer.ui.feature.downloadlanguage.DownloadLanguageViewModel
import com.lukeneedham.languagetransfer.ui.feature.home.HomeViewModel
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonSpecificViewModelFactory
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonViewModel
import com.lukeneedham.languagetransfer.ui.feature.startup.StartupViewModel
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.player.MediaControllerProvider
import com.lukeneedham.languagetransfer.ui.player.PlaybackRepository
import com.lukeneedham.languagetransfer.ui.util.sfx.AppSoundEffectPlayer
import com.lukeneedham.languagetransfer.util.DebugOptions
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModules {
    val modules = listOf(
        domain,
        player,
        sfx,
        debug,
        network,
        database,
        prefs,
        repository,
        viewModels,
    )

    private val domain
        get() = module {
            single { LessonPausepointProviderCache(get()) }
        }

    private val player
        get() = module {
            factory { MediaControllerProvider(androidContext()) }
            single {
                PlaybackRepository()
            }
            factory {
                AudioPlayerProvider(
                    mediaControllerProvider = get(),
                    playbackRepository = get(),
                )
            }
        }

    private val sfx
        get() = module {
            single { AppSoundEffectPlayer() }
        }

    private val debug
        get() = module {
            single { DebugOptions(get()) }
        }

    private val network
        get() = module {
            factory { FileDownloader(androidContext()) }
        }

    private val database
        get() = module {
            single { AppDatabase.build(androidContext()) }
            single { get<AppDatabase>().completedLessonDao() }
        }

    private val prefs
        get() = module {
            single { DebugPreferencesDao(androidContext()) }
            single { PausepointModificationsDao(androidContext()) }
        }

    private val repository
        get() = module {
            factory { PausePointsRepository(androidContext()) }
            factory { LessonNamesRepository(androidContext()) }
            single { AudioLessonRepository(get(), get(), get()) }
            factory { LanguageDownloadRepository(get(), androidContext()) }
            single { CompletedLessonRepository(get()) }
        }

    private val viewModels
        get() = module {
            viewModel { DebugViewModel(get(), get(), get()) }
            viewModel { StartupViewModel(get()) }
            viewModel { DownloadLanguageViewModel(get()) }
            viewModel { HomeViewModel(get(), get(), get()) }
            viewModel { (lesson: CourseLesson) ->
                LessonViewModel(
                    initialLesson = lesson,
                    lessonSpecificViewModelFactory = get(),
                    debugOptions = get(),
                    playbackRepository = get(),
                )
            }

            // VM factory for 'fake' VMs
            factory {
                LessonSpecificViewModelFactory(
                    completedLessonRepository = get(),
                    lessonPausepointProviderCache = get(),
                    audioPlayerProvider = get(),
                    audioLessonRepository = get(),
                    soundEffectPlayer = get(),
                    debugOptions = get(),
                )
            }
        }
}
