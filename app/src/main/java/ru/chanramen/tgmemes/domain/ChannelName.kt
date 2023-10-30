package ru.chanramen.tgmemes.domain

@JvmInline
value class ChannelName(val name: String) {
    // TODO: validation (and maybe parsing)

    fun createPreviewUrl() = "https://t.me/s/$name"
}

fun String.toChannelName() = ChannelName(this)