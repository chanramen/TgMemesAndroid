package ru.chanramen.tgmemes.analytics

import ru.chanramen.tgmemes.analytics.api.Analytics
import ru.chanramen.tgmemes.analytics.api.WidgetAnalytics
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics

class AnalyticsImpl(
    private val workAnalytics: Set<WorkAnalytics>,
    private val widgetAnalytics: Set<WidgetAnalytics>,
) : Analytics {
    override fun trackWorkStatus(
        workResult: WorkAnalytics.WorkResult,
        isForce: Boolean,
        channelName: String?,
        period: Long?,
        info: WorkAnalytics.ResultInfo,
    ) {
        workAnalytics.forEach {
            it.trackWorkStatus(
                workResult = workResult,
                isForce = isForce,
                channelName = channelName,
                period = period,
                info = info,
            )
        }
    }

    override fun widgetCreated(id: Long) {
        widgetAnalytics.forEach {
            it.widgetCreated(id)
        }
    }

    override fun widgetSettingsChanged(
        id: Long,
        prevChannelName: String,
        prevUpdatePeriod: Int,
        newChannelName: String,
        newUpdatePeriod: Int,
    ) {
        widgetAnalytics.forEach {
            it.widgetSettingsChanged(
                id = id,
                prevChannelName = prevChannelName,
                prevUpdatePeriod = prevUpdatePeriod,
                newChannelName = newChannelName,
                newUpdatePeriod = newUpdatePeriod,
            )
        }
    }

    override fun widgetDeleted(id: Long, channelName: String) {
        widgetAnalytics.forEach {
            it.widgetDeleted(id, channelName)
        }
    }

}