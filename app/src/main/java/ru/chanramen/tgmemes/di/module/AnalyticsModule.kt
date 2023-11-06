package ru.chanramen.tgmemes.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.chanramen.tgmemes.analytics.AnalyticsImpl
import ru.chanramen.tgmemes.analytics.api.Analytics
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalytics(
        workAnalytics: Set<@JvmSuppressWildcards WorkAnalytics>,
        widgetAnalytics: Set<@JvmSuppressWildcards WidgetAnalytics>,
    ): Analytics = AnalyticsImpl(workAnalytics, widgetAnalytics)
}