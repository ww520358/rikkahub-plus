package me.rerere.rikkahub.data.db.dao.cluster

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.rerere.rikkahub.data.db.entity.cluster.ClusterEntity

@Dao
interface ClusterDao {
    @Query("SELECT * FROM cluster_definitions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ClusterEntity>>

    @Query("SELECT * FROM cluster_definitions WHERE id = :id")
    suspend fun getById(id: Int): ClusterEntity?

    @Insert
    suspend fun insert(cluster: ClusterEntity): Long

    @Update
    suspend fun update(cluster: ClusterEntity)

    @Delete
    suspend fun delete(cluster: ClusterEntity)
}
