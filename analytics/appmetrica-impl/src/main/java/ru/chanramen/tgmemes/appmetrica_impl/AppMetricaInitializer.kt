package ru.chanramen.tgmemes.appmetrica_impl

import android.content.Context
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import ru.chanramen.tgmemes.analytics.api.AnalyticsInitializer

class AppMetricaInitializer(private val context: Context) : AnalyticsInitializer {
    override fun initialize() {
        AppMetrica.activate(
            context,
            AppMetricaConfig.newConfigBuilder(BuildConfig.APPMETRICA_KEY).build()
        )
    }
}