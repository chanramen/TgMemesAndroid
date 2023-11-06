package ru.chanramen.tgmemes.appmetrica_impl.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.chanramen.tgmemes.analytics.api.AnalyticsInitializer
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics
import ru.chanramen.tgmemes.appmetrica_impl.AppMetricaInitializer
import ru.chanramen.tgmemes.appmetrica_impl.WidgetAnalyticsImpl
import ru.chanramen.tgmemes.appmetrica_impl.WorkAnalyticsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {
    @Provides
    @Singleton
    @IntoSet
    fun provideInitializer(@ApplicationContext context: Context): AnalyticsInitializer =
        AppMetricaInitializer(context)

    @Provides
    @Singleton
    @IntoSet
    fun provideWidgetAnalytics(): WidgetAnalytics = WidgetAnalyticsImpl()

    @Provides
    @Singleton
    @IntoSet
    fun provideWorkAnalytics(): WorkAnalytics = WorkAnalyticsImpl()
}