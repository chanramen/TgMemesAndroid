package ru.chanramen.tgmemes.analytics.firebase_impl

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import ru.chanramen.tgmemes.analytics.api.WorkAnalytics

class WorkAnalyticsImpl(private val firebaseAnalytics: FirebaseAnalytics) : WorkAnalytics {
    override fun trackWorkStatus(
        workResult: WorkAnalytics.WorkResult,
        isForce: Boolean,
        channelName: String?,
        period: Long?,
        info: WorkAnalytics.ResultInfo,
    ) {
        firebaseAnalytics.logEvent("work_status") {
            param("work_result", workResult.toParamValue())
            param("is_force", if (isForce) 1 else 0)
            channelName?.let { param("channel_name", it) }
            period?.let { param("period", it) }
            param("info", info.toParamValue())
        }
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