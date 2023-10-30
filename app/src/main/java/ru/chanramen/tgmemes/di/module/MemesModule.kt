package ru.chanramen.tgmemes.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import ru.chanramen.tgmemes.data.memes.MemesRepository

@Module
@InstallIn(SingletonComponent::class)
class MemesModule {
    @Provides
    fun provideMemesRepository(client: HttpClient) = MemesRepository(client)
}