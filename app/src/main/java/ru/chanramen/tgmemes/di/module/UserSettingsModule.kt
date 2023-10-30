package ru.chanramen.tgmemes.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.chanramen.tgmemes.data.AppDatabase
import ru.chanramen.tgmemes.data.settings.SettingsRepository
import ru.chanramen.tgmemes.data.settings.db.UserSettingsDao

@Module
@InstallIn(SingletonComponent::class)
class UserSettingsModule {

    @Provides
    fun provideUserSettingsDao(database: AppDatabase) = database.userSettingsDao()

    @Provides
    fun provideUserSettingsRepository(userSettingsDao: UserSettingsDao) =
        SettingsRepository(userSettingsDao)
}