package ru.chanramen.tgmemes.data.memes

sealed interface MemeInfoResult {
    class Success(val result: MemeInfo) : MemeInfoResult
    data object EmptyData : MemeInfoResult
}