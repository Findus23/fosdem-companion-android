package org.matomocamp.companion.utils

import androidx.appcompat.app.AppCompatDelegate
import org.matomocamp.companion.settings.UserSettingsProvider
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Automatically switches the light/dark theme when the user updates its preferences.
 */
@Singleton
class ThemeManager @Inject constructor(userSettingsProvider: UserSettingsProvider) {

    init {
        BackgroundWorkScope.launch {
            userSettingsProvider.theme.collect { theme ->
                AppCompatDelegate.setDefaultNightMode(theme ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
}