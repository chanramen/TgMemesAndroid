package ru.chanramen.tgmemes.debug

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.launch
import ru.chanramen.tgmemes.TgMemesApp
import ru.chanramen.tgmemes.TgMemesWidget
import ru.chanramen.tgmemes.data.memes.MemeInfoResult
import ru.chanramen.tgmemes.data.memes.MemesRepository
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.widget.toWidgetPrefs
import ru.chanramen.tgmemes.data.worker.MemeWorker

class WidgetRestartDebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(this@WidgetRestartDebugActivity)
            glanceAppWidgetManager
                .getGlanceIds(TgMemesWidget::class.java)
                .forEach { glanceId ->
                    val prefs = getAppWidgetState(this@WidgetRestartDebugActivity, PreferencesGlanceStateDefinition, glanceId).toWidgetPrefs()
                    val id = prefs.getId() ?: return@forEach
                    val request = OneTimeWorkRequestBuilder<MemeWorker>()
                        .setInputData(MemeWorker.Params(id, true).toData())
                        .build()
                    WorkManager.getInstance(this@WidgetRestartDebugActivity).enqueue(request)
                }
            finish()
        }
    }
}