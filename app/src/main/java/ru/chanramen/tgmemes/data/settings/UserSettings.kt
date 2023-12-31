package ru.chanramen.tgmemes.data.settings

import ru.chanramen.tgmemes.BuildConfig
import ru.chanramen.tgmemes.domain.ChannelName

data class UserSettings(
    val publicName: ChannelName,
    val updatePeriod: Long,
    val id: Long = 0,
    val lastUpdateTime: Long = 0,
) {
    companion object {
        fun default() = UserSettings(
            ChannelName(BuildConfig.DEFAULT_CHANNEL_NAME),
            BuildConfig.DEFAULT_UPDATE_TIME,
        )
    }
}

fun UserSettings?.orDefault() = this ?: UserSettings.default()
