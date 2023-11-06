package ru.chanramen.tgmemes.analytics.firebase_impl

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics

private const val WIDGET_SETTINGS_CHANGED_EVENT_NAME = "widget_settings_changed"
private const val WIDGET_CREATED_EVENT_NAME = "widget_created"
private const val WIDGET_DELETED_EVENT_NAME = "widget_deleted"

private const val WIDGET_ID_PARAM = "widget_id"
private const val WIDGET_PREV_CHANNEL_NAME_PARAM = "prev_channel_name"
private const val WIDGET_PREV_UPDATE_PERIOD_PARAM = "prev_update_period"
private const val WIDGET_NEW_CHANNEL_NAME_PARAM = "new_channel_name"
private const val WIDGET_NEW_UPDATE_PERIOD_PARAM = "new_update_period"
private const val WIDGET_CHANNEL_NAME_PARAM = "channel_name"


class WidgetAnalyticsImpl(private val firebaseAnalytics: FirebaseAnalytics) : WidgetAnalytics {
    override fun widgetCreated(id: Long) {
        firebaseAnalytics.logEvent(WIDGET_CREATED_EVENT_NAME) {
            param(WIDGET_ID_PARAM, id)
        }
    }

    override fun widgetSettingsChanged(
        id: Long,
        prevChannelName: String,
        prevUpdatePeriod: Int,
        newChannelName: String,
        newUpdatePeriod: Int,
    ) {
        firebaseAnalytics.logEvent(WIDGET_SETTINGS_CHANGED_EVENT_NAME) {
            param(WIDGET_ID_PARAM, id)
            param(WIDGET_PREV_CHANNEL_NAME_PARAM, prevChannelName)
            param(WIDGET_PREV_UPDATE_PERIOD_PARAM, prevUpdatePeriod.toLong())
            param(WIDGET_NEW_CHANNEL_NAME_PARAM, newChannelName)
            param(WIDGET_NEW_UPDATE_PERIOD_PARAM, newUpdatePeriod.toLong())
        }
    }

    override fun widgetDeleted(id: Long, channelName: String) {
        firebaseAnalytics.logEvent(WIDGET_DELETED_EVENT_NAME) {
            param(WIDGET_ID_PARAM, id)
            param(WIDGET_CHANNEL_NAME_PARAM, channelName)
        }
    }
}