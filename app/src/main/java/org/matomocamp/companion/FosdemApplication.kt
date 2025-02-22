package org.matomocamp.companion

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.multidex.MultiDex
import org.matomocamp.companion.alarms.AppAlarmManager
import org.matomocamp.companion.utils.ThemeManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class FosdemApplication : Application() {

    // Injected for automatic initialization on app startup

    @Inject
    lateinit var themeManager: ThemeManager

    @Inject
    lateinit var alarmManager: AppAlarmManager

    // Preload UI State SharedPreferences for faster initial access
    @Inject
    @Named("UIState")
    lateinit var preferences: SharedPreferences

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}