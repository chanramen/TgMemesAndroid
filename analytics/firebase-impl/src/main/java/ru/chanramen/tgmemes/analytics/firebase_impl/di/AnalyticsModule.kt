package ru.chanramen.tgmemes.analytics.firebase_impl.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics
import ru.chanramen.tgmemes.analytics.firebase_impl.WidgetAnalyticsImpl
import ru.chanramen.tgmemes.analytics.firebase_impl.WorkAnalyticsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AnalyticsModule {
    @Singleton
    @Provides
    fun provideFirebaseAnalytics() = Firebase.analytics

    @Singleton
    @Provides
    @IntoSet
    fun provideWorkAnalytics(firebaseAnalytics: FirebaseAnalytics): WorkAnalytics =
        WorkAnalyticsImpl(firebaseAnalytics)

    @Singleton
    @Provides
    @IntoSet
    fun provideWidgetAnalytics(firebaseAnalytics: FirebaseAnalytics): WidgetAnalytics =
        WidgetAnalyticsImpl(firebaseAnalytics)
}