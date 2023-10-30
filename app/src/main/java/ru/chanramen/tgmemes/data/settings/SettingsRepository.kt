package ru.chanramen.tgmemes.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.chanramen.tgmemes.data.settings.db.UserSettingsDao
import ru.chanramen.tgmemes.data.settings.db.toUserSettingsDb
import ru.chanramen.tgmemes.data.settings.db.toUserSettingsModel
import ru.chanramen.tgmemes.domain.toChannelName


class SettingsRepository(private val settingsDao: UserSettingsDao) {
    suspend fun saveSettings(settings: UserSettings) = settings.toUserSettingsDb()
        .let { settingsDao.insertUserSettings(it) }
        .let { settingsDao.getById(it) }
        ?.toUserSettingsModel()

    suspend fun getSettingsById(id: Long) = settingsDao.getById(id)
        ?.toUserSettingsModel()

    suspend fun updateSettings(settings: UserSettings) = settings.toUserSettingsDb()
        .let {
            settingsDao.updateUserSettings(it)
            getSettingsById(settings.id)
        }

    suspend fun deleteSettings(settings: UserSettings) =
        settingsDao.deleteUserSettings(settings.toUserSettingsDb())
}