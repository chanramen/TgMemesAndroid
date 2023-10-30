package ru.chanramen.tgmemes.data.settings.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.chanramen.tgmemes.domain.ChannelName

import ru.chanramen.tgmemes.data.settings.UserSettings as UserSettingsModel

@Entity
data class UserSettings(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val channelName: String,
    val updatePeriod: Long,
    val lastUpdateTime: Long,
)

fun UserSettingsModel.toUserSettingsDb() = UserSettings(
    id = id,
    channelName = publicName.name,
    updatePeriod = updatePeriod,
    lastUpdateTime = lastUpdateTime,
)

fun UserSettings.toUserSettingsModel() = UserSettingsModel(
    id = id,
    publicName = ChannelName(channelName),
    updatePeriod = updatePeriod,
    lastUpdateTime = lastUpdateTime,
)