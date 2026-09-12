package me.rerere.rikkahub.data.db.dao.plugin

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.plugin.PluginEntity

@Dao
interface PluginDao {
    @Query("SELECT * FROM plugins ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PluginEntity>>

    @Query("SELECT * FROM plugins WHERE enabled = 1")
    suspend fun getEnabled(): List<PluginEntity>

    @Query("SELECT * FROM plugins WHERE id = :id")
    suspend fun getById(id: Int): PluginEntity?

    @Insert
    suspend fun insert(plugin: PluginEntity): Long

    @Update
    suspend fun update(plugin: PluginEntity)

    @Delete
    suspend fun delete(plugin: PluginEntity)
}
