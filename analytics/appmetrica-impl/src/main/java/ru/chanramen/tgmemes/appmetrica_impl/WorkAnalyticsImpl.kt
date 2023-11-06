package ru.chanramen.tgmemes.appmetrica_impl

import io.appmetrica.analytics.AppMetrica
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics

class WorkAnalyticsImpl : WorkAnalytics {
    override fun trackWorkStatus(
        workResult: WorkAnalytics.WorkResult,
        isForce: Boolean,
        channelName: String?,
        period: Long?,
        info: WorkAnalytics.ResultInfo,
    ) {
        AppMetrica.reportEvent(
            "work_status",
            mapOf(
                "work_result" to workResult.toParamValue(),
                "is_force" to isForce,
                "channel_name" to channelName,
                "period" to period,
                "info" to info.toParamValue()
            )
        )
    }

    private fun WorkAnalytics.WorkResult.toParamValue() = when (this) {
        WorkAnalytics.WorkResult.SUCCESS -> "success"
        WorkAnalytics.WorkResult.FAILURE -> "failure"
        WorkAnalytics.WorkResult.RETRY -> "retry"
    }

    private fun WorkAnalytics.ResultInfo.toParamValue() = when (this) {
        WorkAnalytics.ResultInfo.OK -> "ok"
        WorkAnalytics.ResultInfo.INVALID_ID -> "invalid_id"
        WorkAnalytics.ResultInfo.SETTINGS_NOT_FOUND -> "settings_not_found"
        WorkAnalytics.ResultInfo.WIDGET_NOT_FOUND -> "widget_not_found"
        WorkAnalytics.ResultInfo.UPDATE_NOT_NEEDED -> "update_not_needed"
        WorkAnalytics.ResultInfo.MEME_GET_FAILURE -> "meme_get_failure"
    }
}