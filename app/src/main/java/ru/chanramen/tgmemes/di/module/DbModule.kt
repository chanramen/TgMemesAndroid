package ru.chanramen.tgmemes.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.chanramen.tgmemes.data.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database")
            .build()

}