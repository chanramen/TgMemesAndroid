package ru.chanramen.tgmemes.data.worker

import android.content.Context
import android.util.Base64
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import ru.chanramen.tgmemes.TgMemesApp
import ru.chanramen.tgmemes.TgMemesWidget
import ru.chanramen.tgmemes.data.memes.MemeInfoResult
import ru.chanramen.tgmemes.data.memes.MemesRepository
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.settings.UserSettings
import ru.chanramen.tgmemes.data.widget.toWidgetPrefs
import timber.log.Timber
import java.time.Duration
import java.util.concurrent.TimeUnit

class MemeWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val params = inputData.parse()
        Timber.d("starting worker with params $params")
        if (params.id <= 0) return Result.failure()

        val settingsRepository = SettingsRepository(app.database.userSettingsDao())
        val memesRepository = MemesRepository(HttpClient(CIO) {
            install(Logging) {
                logger = Logger.ANDROID
            }
        })

        val userSettings =
            settingsRepository.getSettingsById(params.id)
                .also { Timber.d("got user settings $it") }
                ?: return Result.retry()
        // TODO: handle time and timezone changes
        val currentTimeSeconds = System.currentTimeMillis() / 1000L
        if (userSettings.lastUpdateTime + userSettings.updatePeriod < currentTimeSeconds && !params.forceRefresh) {
            return Result.success()
        }
        val meme = memesRepository.loadLastMeme(userSettings.publicName)
        Timber.d("got meme $meme")
        if (meme !is MemeInfoResult.Success) return Result.retry()

        val glanceAppWidgetManager = GlanceAppWidgetManager(applicationContext)
        val widgetId = glanceAppWidgetManager.getGlanceIds(TgMemesWidget::class.java).find {
            val state = getAppWidgetState(
                applicationContext,
                PreferencesGlanceStateDefinition,
                glanceId = it
            )
            state.toWidgetPrefs().getId() == params.id
        } ?: return Result.failure().also {
            Timber.i("cannot find widget id for settings, deleting $userSettings")
            settingsRepository.deleteSettings(userSettings)
        }

        updateAppWidgetState(applicationContext, widgetId) {
            val encodedImage = Base64.encodeToString(meme.result.image, Base64.NO_WRAP)
            it.toWidgetPrefs().setImage(encodedImage)
        }
        TgMemesWidget().update(applicationContext, widgetId)
        settingsRepository.updateSettings(userSettings.copy(lastUpdateTime = currentTimeSeconds))
        return Result.success()
    }

    companion object {
        private const val ITEM_ID_KEY = "ITEM_ID"
        private const val FORCE_REFRESH_KEY = "FORCE_REFRESH"
        private fun Data.parse() = Params(
            getLong(ITEM_ID_KEY, 0),
            getBoolean(FORCE_REFRESH_KEY, false)
        )
    }

    data class Params(
        val id: Long,
        val forceRefresh: Boolean = false,
    ) {
        fun toData() = workDataOf(
            ITEM_ID_KEY to id,
            FORCE_REFRESH_KEY to forceRefresh,
        )
    }

    private inline val app
        get() = this.applicationContext as TgMemesApp
}

fun Context.enqueuePeriodicallyFor(userSettings: UserSettings, withForceUpdate: Boolean = false) {
    val workManager = WorkManager.getInstance(this)
    if (withForceUpdate) {
        val request = OneTimeWorkRequestBuilder<MemeWorker>()
            .setInputData(MemeWorker.Params(userSettings.id, true).toData())
            .build()
        workManager.enqueue(request)
    }
    val request =
        PeriodicWorkRequestBuilder<MemeWorker>(
            userSettings.updatePeriod,
            TimeUnit.SECONDS,
            userSettings.updatePeriod / 10L,
            TimeUnit.SECONDS,
        )
            .setInputData(MemeWorker.Params(userSettings.id, false).toData())
            .setConstraints(Constraints(NetworkType.UNMETERED))
            .build()
    workManager.enqueueUniquePeriodicWork(
        "MEME_WORK_${userSettings.id}",
        ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}