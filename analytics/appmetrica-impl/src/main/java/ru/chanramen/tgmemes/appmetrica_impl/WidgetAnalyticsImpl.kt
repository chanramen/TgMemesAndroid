package ru.chanramen.tgmemes.appmetrica_impl

import io.appmetrica.analytics.AppMetrica
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics

class WidgetAnalyticsImpl : WidgetAnalytics {
    override fun widgetCreated(id: Long) {
        AppMetrica.reportEvent("widget_created", mapOf("widget_id" to id))
    }

    override fun widgetSettingsChanged(
        id: Long,
        prevChannelName: String,
        prevUpdatePeriod: Int,
        newChannelName: String,
        newUpdatePeriod: Int,
    ) {
        AppMetrica.reportEvent(
            "widget_settings_changed",
            mapOf(
                "widget_id" to id,
                "prev_channel_name" to prevChannelName,
                "prev_update_period" to prevUpdatePeriod,
                "new_channel_name" to newChannelName,
                "new_update_period" to newUpdatePeriod,
            )
        )
    }

    override fun widgetDeleted(id: Long, channelName: String) {
        AppMetrica.reportEvent(
            "widget_deleted",
            mapOf("widget_id" to id, "channel_name" to channelName),
        )
    }
}