package ru.chanramen.tgmemes.analytics.api

interface WorkAnalytics {
    fun trackWorkStatus(
        workResult: WorkResult,
        isForce: Boolean,
        channelName: String? = null,
        period: Long? = null,
        info: ResultInfo,
    )

    enum class WorkResult {
        SUCCESS,
        FAILURE,
        RETRY,
    }

    enum class ResultInfo {
        OK,
        INVALID_ID,
        SETTINGS_NOT_FOUND,
        WIDGET_NOT_FOUND,
        UPDATE_NOT_NEEDED,
        MEME_GET_FAILURE,
    }
}