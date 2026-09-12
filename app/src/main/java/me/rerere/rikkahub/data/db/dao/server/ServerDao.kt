package me.rerere.rikkahub.data.db.dao.server

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.server.ServerEntity

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ServerEntity>>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getById(id: Int): ServerEntity?

    @Insert
    suspend fun insert(server: ServerEntity): Long

    @Update
    suspend fun update(server: ServerEntity)

    @Delete
    suspend fun delete(server: ServerEntity)
}
