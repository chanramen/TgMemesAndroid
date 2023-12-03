package ru.chanramen.tgmemes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.hoko.blur.HokoBlur
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.chanramen.tgmemes.analytics.api.Analytics
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.settings.UserSettings
import ru.chanramen.tgmemes.data.widget.toWidgetPrefs
import ru.chanramen.tgmemes.data.worker.enqueuePeriodicallyFor
import ru.chanramen.tgmemes.data.worker.stopAllWorkForId
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [TgMemesWidgetConfigureActivity]
 */
@AndroidEntryPoint
class TgMemesWidgetReceiver : GlanceAppWidgetReceiver() {

    @Inject
    lateinit var settingsRepository: SettingsRepository
    @Inject
    lateinit var analytics: Analytics

    override val glanceAppWidget = TgMemesWidget().apply {
        settingsRepositoryProvider = { settingsRepository }
        analyticsInitializer = { analytics }
    }
}

class TgMemesWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    lateinit var settingsRepositoryProvider: () -> SettingsRepository
    lateinit var analyticsInitializer: () -> Analytics
    private val settingsRepository by lazy(LazyThreadSafetyMode.NONE) {
        settingsRepositoryProvider()
    }
    private val analytics: Analytics by lazy(LazyThreadSafetyMode.NONE) {
        analyticsInitializer()
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId).toWidgetPrefs()
        val id = prefs.getId() ?: return
        val userSettings = settingsRepository.getSettingsById(id)
        context.stopAllWorkForId(id)
        userSettings?.let { settingsRepository.deleteSettings(it) }
        analytics.widgetDeleted(id, userSettings?.publicName?.name ?: "")
    }

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

            when (state) {
                is WidgetState.NotConfigured -> {
                    LaunchedEffect(key1 = state) {
                        Timber.i("widget $id: not configured")
                        settingsRepository.saveSettings(UserSettings.default())
                            ?.let { userSettings ->
                                updateAppWidgetState(context, id) {
                                    it.toWidgetPrefs().setId(userSettings.id)
                                }
                                context.enqueuePeriodicallyFor(userSettings, true)
                                analytics.widgetCreated(userSettings.id)
                            }
                    }
                }

                is WidgetState.NoImage -> {
                    LaunchedEffect(key1 = state) {
                        Timber.i("widget $id: no image, settingsId=$settingsId")
                        settingsRepository.getSettingsById(settingsId)
                            ?.let { context.enqueuePeriodicallyFor(it, true) }
                    }
                }

                is WidgetState.Data -> {
                    LaunchedEffect(key1 = state) {
                        Timber.d("start drawing for widget with settingsId=$settingsId")
                    }
                }
            }

            GlanceTheme {
                Box(
                    modifier = GlanceModifier.fillMaxSize().background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (state is WidgetState.Data) {
                        var blurredImageState by remember { mutableStateOf(state.image) }
                        LaunchedEffect(state.image) {
                            withContext(Dispatchers.Default) {
                                blurredImageState = blurImage(context, state.image)
                            }
                        }
                        Image(
                            provider = ImageProvider(blurredImageState),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = GlanceModifier.appWidgetBackground().fillMaxSize(),
                        )
                        Image(
                            provider = ImageProvider(state.image),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier.fillMaxSize(),
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
                if (BuildConfig.DEBUG) {
                    Text(text = "Debug", style = TextStyle(color = ColorProvider(Color.Red)))
                }
            }
        }
    }

    private fun blurImage(
        context: Context,
        image: Bitmap,
    ): Bitmap = HokoBlur.with(context)
        .sampleFactor(2.0f)
        .radius(15)
        .blur(image)

    private sealed interface WidgetState {
        data object NotConfigured : WidgetState
        data object NoImage : WidgetState
        data class Data(
            val id: Long,
            val image: Bitmap,
        )
    }
}