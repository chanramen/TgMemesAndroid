package ru.chanramen.tgmemes

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.chanramen.tgmemes.analytics.api.AnalyticsInitializer
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TgMemesApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var analyticsInitializer: Set<@JvmSuppressWildcards AnalyticsInitializer>

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        analyticsInitializer.forEach(AnalyticsInitializer::initialize)
    }
}