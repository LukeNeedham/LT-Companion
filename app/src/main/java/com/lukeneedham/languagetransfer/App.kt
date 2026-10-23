package com.lukeneedham.languagetransfer

import android.app.Application
import com.lukeneedham.common.DebugKit
import com.lukeneedham.common.DebugKitConfig
import com.lukeneedham.languagetransfer.BuildConfig
import com.lukeneedham.languagetransfer.di.KoinModules
import com.lukeneedham.languagetransfer.ui.util.sfx.AppSoundEffectPlayer
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Custom Application class for initializing Koin dependency injection
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // First, so that Logger and the crash handler are ready before anything else runs
        DebugKit.init(DebugKitConfig(enabled = BuildConfig.DEBUG))

        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(KoinModules.modules)
        }

        // Prepare sound effects as soon as possible
        val appSoundEffectPlayer = get<AppSoundEffectPlayer>()
        appSoundEffectPlayer.prepare(this)
    }
}