package ru.chanramen.tgmemes

import android.app.Application
import androidx.room.Room
import ru.chanramen.tgmemes.data.AppDatabase
import timber.log.Timber

class TgMemesApp : Application() {
    // TODO: use proper di stuff
    val database by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "app-database")
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}