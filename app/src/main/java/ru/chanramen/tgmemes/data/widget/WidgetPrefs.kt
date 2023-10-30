package ru.chanramen.tgmemes.data.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val ID_KEY = longPreferencesKey("ID")
private val IMAGE_KEY = stringPreferencesKey("IMAGE")

open class WidgetPrefs constructor(private val prefs: Preferences) {
    fun getId() = prefs[ID_KEY]

    fun getImage() = prefs[IMAGE_KEY]
}

class WritableWidgetPrefs constructor(private val prefs: MutablePreferences) : WidgetPrefs(prefs) {
    fun setId(id: Long) = prefs.set(ID_KEY, id)

    fun setImage(base64Image: String) = prefs.set(IMAGE_KEY, base64Image)
}

fun Preferences.toWidgetPrefs() = WidgetPrefs(this)

fun MutablePreferences.toWidgetPrefs() = WritableWidgetPrefs(this)

