package ru.chanramen.tgmemes.data.settings.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(userSettings: UserSettings): Long

    @Query("SELECT * FROM UserSettings WHERE id = :id")
    suspend fun getById(id: Long): UserSettings?

    @Update
    suspend fun updateUserSettings(userSettings: UserSettings)

    @Delete
    suspend fun deleteUserSettings(userSettings: UserSettings)
}