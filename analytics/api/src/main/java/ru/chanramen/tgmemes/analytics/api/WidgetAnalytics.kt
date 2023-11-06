package ru.chanramen.tgmemes.analytics.api

interface WidgetAnalytics {
    fun widgetCreated(id: Long)

    fun widgetSettingsChanged(
        id: Long,
        prevChannelName: String,
        prevUpdatePeriod: Int,
        newChannelName: String,
        newUpdatePeriod: Int
    )

    fun widgetDeleted(id: Long, channelName: String)
}