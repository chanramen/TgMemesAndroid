package ru.chanramen.tgmemes.data.memes

data class MemeInfo(
    val image: ByteArray,
    val author: String?,
    val views: Int?,
    val postUrl: String?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MemeInfo

        if (!image.contentEquals(other.image)) return false
        if (author != other.author) return false
        if (views != other.views) return false
        if (postUrl != other.postUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image.contentHashCode()
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (views ?: 0)
        result = 31 * result + (postUrl?.hashCode() ?: 0)
        return result
    }
}
