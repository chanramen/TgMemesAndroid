package ru.chanramen.tgmemes.data.settings

import ru.chanramen.tgmemes.data.settings.db.UserSettingsDao
import ru.chanramen.tgmemes.data.settings.db.toUserSettingsDb
import ru.chanramen.tgmemes.data.settings.db.toUserSettingsModel


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