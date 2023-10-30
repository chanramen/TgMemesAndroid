package ru.chanramen.tgmemes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.work.WorkManager
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.settings.UserSettings
import ru.chanramen.tgmemes.data.widget.toWidgetPrefs
import ru.chanramen.tgmemes.data.worker.enqueuePeriodicallyFor
import timber.log.Timber

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [TgMemesWidgetConfigureActivity]
 */
class TgMemesWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = TgMemesWidget()
}

class TgMemesWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {


        provideContent {
            val prefs = currentState<Preferences>().toWidgetPrefs()
            val settingsId = prefs.getId() ?: 0
            val imageBase64 = prefs.getImage()
            val state = when {
                settingsId <= 0 -> WidgetState.NotConfigured
                imageBase64.isNullOrBlank() -> WidgetState.NotConfigured
                else -> {
                    try {
                        val bytes = Base64.decode(imageBase64, Base64.NO_WRAP)
                        val bitmap = BitmapFactory.decodeByteArray(
                            bytes,
                            0,
                            bytes.size
                        )
                        WidgetState.Data(settingsId, bitmap)
                    } catch (e: Throwable) {
                        WidgetState.NoImage
                    }
                }
            }

            if (state is WidgetState.NotConfigured) {
                LaunchedEffect(key1 = state) {
                    Timber.i("widget $id: not configured")
                    val settingsRepository =
                        SettingsRepository((context.applicationContext as TgMemesApp).database.userSettingsDao())
                    settingsRepository.saveSettings(UserSettings.default())
                        ?.let { userSettings ->
                            updateAppWidgetState(context, id) {
                                it.toWidgetPrefs().setId(userSettings.id)
                            }
                            context.enqueuePeriodicallyFor(userSettings, true)
                        }
                }
            } else if (state is WidgetState.NoImage) {
                LaunchedEffect(key1 = state) {
                    Timber.i("widget $id: no image, settingsId=$settingsId")
                    val settingsRepository =
                        SettingsRepository((context.applicationContext as TgMemesApp).database.userSettingsDao())
                    settingsRepository.getSettingsById(settingsId)
                        ?.let { context.enqueuePeriodicallyFor(it, true) }
                }
            } else if (state is WidgetState.Data) {
                LaunchedEffect(key1 = state) {
                    Timber.d("start drawing for widget with settingsId=$settingsId")
                }
            }

            GlanceTheme {
                Box(
                    modifier = GlanceModifier.fillMaxSize().background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (state is WidgetState.Data) {
                        Image(
                            provider = ImageProvider(state.image),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    private sealed interface WidgetState {
        data object NotConfigured : WidgetState
        data object NoImage : WidgetState
        data class Data(
            val id: Long,
            val image: Bitmap,
        )
    }
}