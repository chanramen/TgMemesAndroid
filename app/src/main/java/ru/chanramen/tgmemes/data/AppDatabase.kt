package ru.chanramen.tgmemes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.chanramen.tgmemes.data.settings.db.UserSettings
import ru.chanramen.tgmemes.data.settings.db.UserSettingsDao

@Database(entities = [UserSettings::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userSettingsDao(): UserSettingsDao
}