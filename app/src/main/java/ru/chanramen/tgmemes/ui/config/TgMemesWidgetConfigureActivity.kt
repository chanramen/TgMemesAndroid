package ru.chanramen.tgmemes.ui.config

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.chanramen.tgmemes.TgMemesApp
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.settings.UserSettings
import ru.chanramen.tgmemes.data.settings.orDefault
import ru.chanramen.tgmemes.data.widget.toWidgetPrefs
import ru.chanramen.tgmemes.data.worker.enqueuePeriodicallyFor
import ru.chanramen.tgmemes.domain.ChannelName
import ru.chanramen.tgmemes.domain.toChannelName
import ru.chanramen.tgmemes.ui.theme.TgMemesTheme


class TgMemesWidgetConfigureActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    public override fun onCreate(icicle: Bundle?) {
        val style = SystemBarStyle.auto(Color.GREEN, Color.TRANSPARENT)
        enableEdgeToEdge(
            style,
            style,
        )
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)
        val glanceAppWidgetManager = GlanceAppWidgetManager(this)
        val widgetId = glanceAppWidgetManager.getGlanceIdBy(intent) ?: run {
            finish()
            return
        }

        val stateFlow = MutableStateFlow<State>(State.Loading)
        val settingsRepository =
            SettingsRepository((application as TgMemesApp).database.userSettingsDao())
        lifecycleScope.launch {
            val widgetPrefs = getAppWidgetState(
                this@TgMemesWidgetConfigureActivity,
                PreferencesGlanceStateDefinition,
                widgetId
            ).toWidgetPrefs()
            val settingsId = widgetPrefs.getId()
            val settings = settingsId?.let { settingsRepository.getSettingsById(it) }.orDefault()

            initData(stateFlow, settings)
        }

        drawView(
            stateFlow,
            { name ->
                stateFlow.value.let {
                    if (it is State.Data) {
                        stateFlow.value = it.copy(currentName = name)
                    }
                }
            },
            { updatePeriod ->
                stateFlow.value.let {
                    if (it is State.Data) {
                        stateFlow.value = it.copy(updatePeriod = updatePeriod)
                    }
                }
            },
            {
                lifecycleScope.launch {
                    val state = stateFlow.value
                    if (state !is State.Data) return@launch
                    stateFlow.value = State.Loading
                    val settings = settingsRepository.saveSettings(
                        state.settings.copy(
                            publicName = ChannelName(state.currentName),
                            updatePeriod = state.updatePeriod,
                        )
                    )
                    if (settings != null) {
                        updateWidget(
                            context = this@TgMemesWidgetConfigureActivity,
                            widgetId = widgetId,
                            userSettings = settings,
                            forceUpdate = settings.publicName != state.settings.publicName,
                        )
                        setResult(RESULT_OK)
                        finish()
                    }
                }
            }
        )
    }

    private fun initData(
        stateFlow: MutableStateFlow<State>,
        settings: UserSettings,
    ) {
        stateFlow.value = State.Data(
            settings,
            settings.publicName.name,
            settings.updatePeriod,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun drawView(
        stateFlow: StateFlow<State>,
        updateName: (String) -> Unit,
        updatePeriod: (Long) -> Unit,
        save: () -> Unit,
    ) {
        setContent {
            TgMemesTheme {
                val state by stateFlow.collectAsState()

                val data = state
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Row {
                                Text(text = "Settings")
                                Spacer(modifier = Modifier.weight(1f))
                                (data as? State.Data)?.let {
                                    IconButton(onClick = save, enabled = it.hasBeenChanged) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Save"
                                        )
                                    }
                                }
                            }
                        })
                    }
                ) { paddings ->
                    if (data is State.Data) {
                        Column(modifier = Modifier.padding(paddings)) {
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                value = data.currentName,
                                onValueChange = updateName
                            )
                            TextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                value = data.updatePeriod.toString(),
                                onValueChange = {
                                    it.toLongOrNull()?.let(updatePeriod)
                                })
                        }
                    }

                    if (data is State.Loading) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }


    private sealed interface State {
        data object Loading : State
        data class Data(
            val settings: UserSettings,
            val currentName: String,
            val updatePeriod: Long,
        ) : State {
            val hasBeenChanged =
                settings.updatePeriod != updatePeriod || settings.publicName.name != currentName
        }
    }
}


private suspend fun updateWidget(
    context: Context,
    widgetId: GlanceId,
    userSettings: UserSettings,
    forceUpdate: Boolean,
) {
    withContext(Dispatchers.IO) {
        updateAppWidgetState(context, widgetId) {
            val prefs = it.toWidgetPrefs()
            prefs.setId(userSettings.id)
        }
    }
    context.enqueuePeriodicallyFor(userSettings, forceUpdate)
}