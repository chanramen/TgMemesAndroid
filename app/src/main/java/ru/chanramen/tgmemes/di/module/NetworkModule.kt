package ru.chanramen.tgmemes.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import ru.chanramen.tgmemes.BuildConfig
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideHttpClient() = HttpClient(CIO) {
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.HEADERS
            logger = object : Logger {
                private val timber = Timber.tag("HTTP Client")
                override fun log(message: String) {
                    timber.i(message)
                }
            }
        }
    }
}